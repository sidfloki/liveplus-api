package com.dramalive.app.models

data class MediaItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val videoUrl: String,
    val category: String // "Movie" or "Series"
)
