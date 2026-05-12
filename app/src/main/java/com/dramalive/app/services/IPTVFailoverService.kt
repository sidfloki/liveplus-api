package com.dramalive.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.dramalive.app.util.XtreamLogicManager
import com.dramalive.app.util.XtreamServer
import com.dramalive.app.util.DirectLinksStore
import com.google.firebase.database.*
import kotlinx.coroutines.*

class IPTVFailoverService : Service() {
    private val TAG = "IPTVFailoverService"
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    
    private lateinit var database: DatabaseReference
    private var availableServers = mutableListOf<XtreamServer>()
    private var currentServerIndex = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "IPTV Failover Service Started")
        
        // ربط Firebase Realtime Database الرابط الصحيح
        database = com.google.firebase.database.FirebaseDatabase.getInstance("https://streamvault-5f4a7-default-rtdb.europe-west1.firebasedatabase.app/").reference
        
        setupFirebaseListeners()
    }

    private fun setupFirebaseListeners() {
        // مراقبة قائمة المصادر (source_list)
        database.child("source_list").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceScope.launch {
                    val servers = mutableListOf<XtreamServer>()
                    for (child in snapshot.children) {
                        val host = child.child("host").getValue(String::class.java) ?: ""
                        val user = child.child("username").getValue(String::class.java) ?: ""
                        val pass = child.child("password").getValue(String::class.java) ?: ""
                        
                        if (host.isNotEmpty()) {
                            val server = XtreamServer(host, user, pass)
                            // الفلترة الذكية (القسم والنشاط)
                            if (XtreamLogicManager.validateAndFilterServer(server)) {
                                servers.add(server)
                            }
                        }
                    }
                    availableServers = servers
                    Log.d(TAG, "Updated available servers: ${availableServers.size}")
                    
                    // إذا لم يكن هناك سيرفر نشط حالياً، ابدأ بالأول
                    if (availableServers.isNotEmpty()) {
                        switchToNextBestServer()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase Cancelled: ${error.message}")
            }
        })
        
        // مراقبة لوحة التحكم الرئيسية (xtream_config) للتحديث اليدوي الفوري
        database.child("xtream_config").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val host = snapshot.child("host").getValue(String::class.java) ?: ""
                val user = snapshot.child("username").getValue(String::class.java) ?: ""
                val pass = snapshot.child("password").getValue(String::class.java) ?: ""
                
                if (host.isNotEmpty() && user.isNotEmpty()) {
                    com.dramalive.app.Config.XTREAM_BASE_URL = host
                    com.dramalive.app.Config.XTREAM_USERNAME = user
                    com.dramalive.app.Config.XTREAM_PASSWORD = pass
                    Log.d(TAG, "Dashboard Update Applied: $host")
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // مراقبة القنوات المباشرة (Direct Links) مثل DramaLive
        database.child("direct_channels").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val directItems = mutableListOf<com.dramalive.app.models.MediaItem>()
                for (child in snapshot.children) {
                    val title = child.child("title").getValue(String::class.java) ?: "Channel"
                    val url = child.child("url").getValue(String::class.java) ?: ""
                    val category = child.child("category").getValue(String::class.java) ?: "Direct"
                    val image = child.child("image").getValue(String::class.java) ?: ""
                    
                    if (url.isNotEmpty()) {
                        directItems.add(com.dramalive.app.models.MediaItem(
                            id = "direct_${child.key}",
                            title = title,
                            description = "Direct Link",
                            imageUrl = image,
                            videoUrl = url,
                            category = category,
                            sourceType = com.dramalive.app.models.SourceType.DIRECT
                        ))
                    }
                }
                DirectLinksStore.updateLinks(directItems)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // مراقبة طلبات التغيير اليدوية أو فشل الاتصال من التطبيق
        database.child("commands/trigger_failover").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) == true) {
                    switchToNextBestServer()
                    database.child("commands/trigger_failover").setValue(false)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun switchToNextBestServer() {
        if (availableServers.isEmpty()) return
        
        if (currentServerIndex >= availableServers.size) {
            currentServerIndex = 0
        }
        
        val nextServer = availableServers[currentServerIndex]
        XtreamLogicManager.applyServer(nextServer)
        
        // تحديث Firebase بالسيرفر العامل حالياً
        val updates = mapOf(
            "host" to nextServer.host,
            "username" to nextServer.user,
            "password" to nextServer.pass,
            "updated_at" to System.currentTimeMillis()
        )
        database.child("current_working_server").setValue(updates)
        
        currentServerIndex++
        Log.d(TAG, "Switched to server: ${nextServer.host}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        Log.d(TAG, "IPTV Failover Service Destroyed")
    }
}
