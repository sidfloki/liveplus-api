package com.dramalive.app.util

import com.dramalive.app.models.MediaItem
import com.dramalive.app.models.SourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object M3UParser {
    
    suspend fun fetchFromUrl(url: String): List<MediaItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MediaItem>()
        try {
            val content = URL(url).readText()
            val lines = content.lines()
            
            var currentTitle = ""
            var currentLogo = ""
            var currentGroup = "General"
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("#EXTINF:")) {
                    // Extract name after comma
                    currentTitle = trimmed.substringAfterLast(",").trim()
                    
                    // Extract logo
                    val logoMatch = Regex("""tvg-logo="([^"]*)"""").find(trimmed)
                    currentLogo = logoMatch?.groupValues?.get(1) ?: ""
                    
                    // Extract group
                    val groupMatch = Regex("""group-title="([^"]*)"""").find(trimmed)
                    currentGroup = groupMatch?.groupValues?.get(1) ?: "General"
                    
                } else if (trimmed.startsWith("http")) {
                    items.add(
                        MediaItem(
                            id = trimmed.hashCode(),
                            title = currentTitle,
                            description = "Live from GitHub",
                            imageUrl = currentLogo,
                            videoUrl = trimmed,
                            category = currentGroup,
                            sourceType = SourceType.DIRECT
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        items
    }
}
