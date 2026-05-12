package com.dramalive.app.models

data class MediaItem(
    val id: Any, // Can be Int or String
    val title: String,
    val description: String,
    val imageUrl: String,
    val videoUrl: String,
    val category: String,
    val rating: String = "",
    val year: String = "",
    val director: String = "",
    val cast: String = "",
    val genre: String = "",
    val containerExtension: String = "mp4",
    val sourceType: SourceType = SourceType.XTREAM // Default to Xtream
)

enum class SourceType {
    XTREAM,     // Channels from Xtream Codes API
    DIRECT,     // Direct M3U8/MP4 links
    YOUTUBE,    // YouTube links (if needed later)
    LOCAL       // Local server links
}
