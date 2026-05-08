package com.dramalive.app.util

import android.content.Context
import android.content.SharedPreferences
import com.dramalive.app.models.MediaItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoritesManager {
    private const val PREFS_NAME = "liveplus_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_HISTORY = "history"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): List<MediaItem> {
        val json = getPrefs(context).getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<MediaItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun toggleFavorite(context: Context, item: MediaItem) {
        val favorites = getFavorites(context).toMutableList()
        val existing = favorites.find { it.id == item.id }
        if (existing != null) {
            favorites.remove(existing)
        } else {
            favorites.add(item)
        }
        getPrefs(context).edit().putString(KEY_FAVORITES, gson.toJson(favorites)).apply()
    }

    fun isFavorite(context: Context, itemId: Any): Boolean {
        return getFavorites(context).any { it.id == itemId }
    }

    fun addToHistory(context: Context, item: MediaItem) {
        val history = getHistory(context).toMutableList()
        history.removeAll { it.id == item.id }
        history.add(0, item)
        if (history.size > 50) history.removeAt(history.size - 1)
        getPrefs(context).edit().putString(KEY_HISTORY, gson.toJson(history)).apply()
    }

    fun getHistory(context: Context): List<MediaItem> {
        val json = getPrefs(context).getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<MediaItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
