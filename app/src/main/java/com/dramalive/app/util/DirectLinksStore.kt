package com.dramalive.app.util

import com.dramalive.app.models.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DirectLinksStore {
    private val _directLinks = MutableStateFlow<List<MediaItem>>(emptyList())
    val directLinks: StateFlow<List<MediaItem>> = _directLinks

    fun updateLinks(links: List<MediaItem>) {
        _directLinks.value = links
    }
}
