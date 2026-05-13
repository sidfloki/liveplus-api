package com.dramalive.app.util

import android.util.Log
import com.dramalive.app.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    suspend fun updateRemoteConfig(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://streamvault-5f4a7-default-rtdb.firebaseio.com/xtream_config.json")
            val connection = url.openConnection()
            val text = connection.getInputStream().bufferedReader().use { it.readText() }
            val json = JSONObject(text)
            
            Config.XTREAM_BASE_URL = json.getString("host")
            Config.XTREAM_USERNAME = json.getString("username")
            Config.XTREAM_PASSWORD = json.getString("password")
            
            Log.d(TAG, "Config updated from remote: ${Config.XTREAM_BASE_URL}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update remote config: ${e.message}")
            false
        }
    }
}
