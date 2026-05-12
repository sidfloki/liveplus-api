package com.dramalive.app.util

import android.util.Log
import com.dramalive.app.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    suspend fun updateRemoteConfig(): Boolean {
        // Disabled to use local Config credentials only as requested
        return false
    }
}
