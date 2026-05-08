package com.dramalive.app.api

import com.dramalive.app.Config
import com.dramalive.app.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class XtreamRepository {
    private val service = RetrofitClient.xtreamService
    private val username = Config.XTREAM_USERNAME
    private val password = Config.XTREAM_PASSWORD

    // Authentication
    suspend fun authenticate(): Result<XtreamAuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = service.authenticate(username, password)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Live TV
    suspend fun getLiveCategories(): Result<List<XtreamCategory>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getLiveCategories(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLiveStreams(): Result<List<XtreamLiveStream>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getLiveStreams(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLiveStreamsByCategory(categoryId: String): Result<List<XtreamLiveStream>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getLiveStreamsByCategory(username, password, categoryId = categoryId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Movies (VOD)
    suspend fun getVodCategories(): Result<List<XtreamCategory>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getVodCategories(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVodStreams(): Result<List<XtreamVodStream>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getVodStreams(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVodStreamsByCategory(categoryId: String): Result<List<XtreamVodStream>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getVodStreamsByCategory(username, password, categoryId = categoryId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Series
    suspend fun getSeriesCategories(): Result<List<XtreamCategory>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getSeriesCategories(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSeries(): Result<List<XtreamSeries>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getSeries(username, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSeriesByCategory(categoryId: String): Result<List<XtreamSeries>> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getSeriesByCategory(username, password, categoryId = categoryId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSeriesInfo(seriesId: String): Result<XtreamSeriesInfo> = withContext(Dispatchers.IO) {
        try {
            Result.success(service.getSeriesInfo(username, password, seriesId = seriesId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Convert Xtream models to app MediaItem
    fun liveStreamToMediaItem(stream: XtreamLiveStream): MediaItem {
        return MediaItem(
            id = stream.streamId,
            title = stream.name,
            description = "Live Channel",
            imageUrl = stream.streamIcon,
            videoUrl = Config.getStreamUrl(stream.streamId.toString()),
            category = "Live",
            rating = "",
            year = ""
        )
    }

    fun vodToMediaItem(vod: XtreamVodStream): MediaItem {
        return MediaItem(
            id = vod.streamId,
            title = vod.name,
            description = "",
            imageUrl = vod.streamIcon,
            videoUrl = Config.getVodUrl(vod.streamId.toString(), vod.containerExtension),
            category = "Movie",
            rating = vod.rating,
            containerExtension = vod.containerExtension
        )
    }

    fun seriesToMediaItem(series: XtreamSeries): MediaItem {
        return MediaItem(
            id = series.seriesId,
            title = series.name,
            description = series.plot,
            imageUrl = series.cover,
            videoUrl = "",
            category = "Series",
            rating = series.rating,
            genre = series.genre
        )
    }
}
