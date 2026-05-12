package com.dramalive.app.util

import android.util.Log
import com.dramalive.app.Config
import com.dramalive.app.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

data class XtreamServer(
    val host: String,
    val user: String,
    val pass: String,
    var priority: Int = 0
)

object XtreamLogicManager {
    private const val TAG = "XtreamLogicManager"
    
    // كلمات البحث المطلوبة في الأقسام
    private val REQUIRED_KEYWORDS = listOf("arabic", "bein", "sport", "cinema", "عربي", "رياضة", "بين")

    /**
     * فحص السيرفر والتحقق من صلاحيته ومحتواه
     */
    suspend fun validateAndFilterServer(server: XtreamServer): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val host = server.host.trimEnd('/')
                val authUrl = "$host/player_api.php?username=${server.user}&password=${server.pass}"
                
                // 1. فحص الصلاحية (Auth & Expire)
                val authResponse = URL(authUrl).readText()
                val json = JSONObject(authResponse)
                val userInfo = json.optJSONObject("user_info") ?: return@withContext false
                
                val status = userInfo.optString("status")
                val expDate = userInfo.optLong("exp_date", 0)
                val currentTime = System.currentTimeMillis() / 1000

                if (status != "Active" || (expDate != 0L && expDate < currentTime)) {
                    Log.d(TAG, "Server ${server.host} is inactive or expired")
                    return@withContext false
                }

                // 2. فحص الأقسام (Content Filtering)
                val catUrl = "$authUrl&action=get_live_categories"
                val catResponse = URL(catUrl).readText()
                val categories = JSONArray(catResponse)
                
                var hasRequiredContent = false
                for (i in 0 until categories.length()) {
                    val catName = categories.getJSONObject(i).optString("category_name").lowercase()
                    if (REQUIRED_KEYWORDS.any { catName.contains(it) }) {
                        hasRequiredContent = true
                        break
                    }
                }

                if (!hasRequiredContent) {
                    Log.d(TAG, "Server ${server.host} filtered out: No required Arabic/Sports content")
                    return@withContext false
                }

                Log.d(TAG, "Server ${server.host} PASSED all checks ✅")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error validating server ${server.host}: ${e.message}")
                false
            }
        }
    }

    /**
     * تحديث الإعدادات النشطة في التطبيق
     */
    fun applyServer(server: XtreamServer) {
        Config.XTREAM_BASE_URL = server.host
        Config.XTREAM_USERNAME = server.user
        Config.XTREAM_PASSWORD = server.pass
        Log.d(TAG, "Applied new server: ${server.host}")
    }
}
