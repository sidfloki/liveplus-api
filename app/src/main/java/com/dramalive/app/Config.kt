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

    // Xtream Codes API Configuration (Backup)
    const val XTREAM_BASE_URL = "http://Sameh68g.geekflarecdn.com/"
    const val XTREAM_USERNAME = "sameh68g"
    const val XTREAM_PASSWORD = "15472848"
    
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
        return if (isLocal) {
            "$BASE_URL/stream/$streamId"
        } else {
            "$XTREAM_BASE_URL/live/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
    
    fun getVodUrl(streamId: String, extension: String = "mp4", isLocal: Boolean = false): String {
        return if (isLocal) {
            "$BASE_URL/stream/$streamId"
        } else {
            "$XTREAM_BASE_URL/movie/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
    
    fun getSeriesUrl(streamId: String, extension: String = "mp4", isLocal: Boolean = false): String {
        return if (isLocal) {
            "$BASE_URL/stream/$streamId"
        } else {
            "$XTREAM_BASE_URL/series/$XTREAM_USERNAME/$XTREAM_PASSWORD/$streamId.$extension"
        }
    }
}