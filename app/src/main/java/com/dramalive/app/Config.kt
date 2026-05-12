package com.dramalive.app

object Config {
    // Global Access Configuration
    // Replace this with your Cloudflare or Ngrok URL (e.g., https://your-server.trycloudflare.com)
    var GLOBAL_SERVER_URL = "https://liveplus-server.example.com"
    
    // Local Server Configuration (Backup for home use)
    var LOCAL_SERVER_IP = "192.168.1.100" 
    const val LOCAL_SERVER_PORT = "3000"
    
    // Determine which URL to use (Global by default for worldwide access)
    val BASE_URL get() = if (GLOBAL_SERVER_URL.startsWith("https")) GLOBAL_SERVER_URL else "http://$LOCAL_SERVER_IP:$LOCAL_SERVER_PORT"

    // Remote Configuration (Automatic Updates via Firebase)
    const val REMOTE_CONFIG_URL = "https://streamvault-5f4a7-default-rtdb.europe-west1.firebasedatabase.app/xtream_config.json"
    const val REMOTE_M3U_URL = "https://raw.githubusercontent.com/sidfloki/liveplus-api/main/playlist.m3u"

    // Xtream Codes API Configuration (User's Working Server)
    var XTREAM_BASE_URL = "http://cineplay.vip:2086"
    var XTREAM_USERNAME = "SmvVyh9Hw4"
    var XTREAM_PASSWORD = "yqQ9HHEpAm"
    
    // AdMob Configuration
    const val ADMOB_APP_ID = "ca-app-pub-7876868777201120~4600108640"
    const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-7876868777201120/7628065382"
    const val ADMOB_BANNER_ID = "ca-app-pub-7876868777201120/7955368441"
    
    // API Endpoints
    const val API_GET_LIVE_STREAMS = "/player_api.php?username=%s&password=%s&action=get_live_streams"
    const val API_GET_LIVE_CATEGORIES = "/player_api.php?username=%s&password=%s&action=get_live_categories"
    const val API_GET_VOD_STREAMS = "/player_api.php?username=%s&password=%s&action=get_vod_streams"
    const val API_GET_VOD_CATEGORIES = "/player_api.php?username=%s&password=%s&action=get_vod_categories"
    const val API_GET_SERIES = "/player_api.php?username=%s&password=%s&action=get_series"
    const val API_GET_SERIES_CATEGORIES = "/player_api.php?username=%s&password=%s&action=get_series_categories"
    const val API_GET_SERIES_INFO = "/player_api.php?username=%s&password=%s&action=get_series_info&series_id=%s"
    const val API_AUTH = "/player_api.php?username=%s&password=%s"
    
    // Local API Endpoints
    const val LOCAL_API_MEDIA = "/api/media"
    
    // Stream URL format
    fun getStreamUrl(streamId: String, extension: String = "m3u8", isLocal: Boolean = false): String {
        val cleanBase = XTREAM_BASE_URL.trimEnd('/')
        return if (isLocal) {
            "${BASE_URL.trimEnd('/')}/stream/$streamId"
        } else {
            "$cleanBase/live/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
    
    fun getVodUrl(streamId: String, extension: String = "mp4", isLocal: Boolean = false): String {
        val cleanBase = XTREAM_BASE_URL.trimEnd('/')
        return if (isLocal) {
            "${BASE_URL.trimEnd('/')}/stream/$streamId"
        } else {
            "$cleanBase/movie/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
    
    fun getSeriesUrl(streamId: String, extension: String = "mp4", isLocal: Boolean = false): String {
        val cleanBase = XTREAM_BASE_URL.trimEnd('/')
        return if (isLocal) {
            "${BASE_URL.trimEnd('/')}/stream/$streamId"
        } else {
            "$cleanBase/series/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
}