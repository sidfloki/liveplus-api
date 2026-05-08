package com.dramalive.app.data

import com.dramalive.app.models.MediaItem

object LocalData {
    val movies = listOf(
        MediaItem(
            id = 1,
            title = "Inception",
            description = "A thief who steals corporate secrets through the use of dream-sharing technology.",
            imageUrl = "https://example.com/inception.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            category = "Movie"
        ),
        MediaItem(
            id = 2,
            title = "The Dark Knight",
            description = "Batman raises the stakes in his war on crime.",
            imageUrl = "https://example.com/darkknight.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            category = "Movie"
        )
    )

    val series = listOf(
        MediaItem(
            id = 101,
            title = "Breaking Bad",
            description = "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine.",
            imageUrl = "https://example.com/breakingbad.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            category = "Series"
        ),
        MediaItem(
            id = 102,
            title = "Game of Thrones",
            description = "Nine noble families fight for control over the lands of Westeros.",
            imageUrl = "https://example.com/got.jpg",
            videoUrl = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            category = "Series"
        )
    )
}
