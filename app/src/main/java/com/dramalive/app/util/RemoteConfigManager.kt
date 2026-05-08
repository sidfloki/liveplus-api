package com.dramalive.app.util

import android.util.Log
import com.dramalive.app.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    suspend fun updateRemoteConfig() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching remote config from: ${Config.REMOTE_CONFIG_URL}")
                val jsonString = URL(Config.REMOTE_CONFIG_URL).readText()
                val json = JSONObject(jsonString)

                Config.XTREAM_BASE_URL = json.getString("host")
                Config.XTREAM_USERNAME = json.getString("username")
                Config.XTREAM_PASSWORD = json.getString("password")

                Log.d(TAG, "Successfully updated Xtream credentials from remote")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update remote config: ${e.message}")
            }
        }
    }
}
