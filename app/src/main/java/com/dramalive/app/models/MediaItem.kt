package com.dramalive.app.models

import com.google.gson.annotations.SerializedName

// Unified media item for display
data class MediaItem(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val category: String = "", // "Movie", "Series", or "Live"
    val rating: String = "",
    val year: String = "",
    val genre: String = "",
    val containerExtension: String = ""
)

// Xtream Codes API Models
data class XtreamCategory(
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("category_name") val categoryName: String = "",
    @SerializedName("parent_id") val parentId: Int = 0
)

data class XtreamLiveStream(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String = "",
    @SerializedName("epg_channel_id") val epgChannelId: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("tv_archive") val tvArchive: Int = 0,
    @SerializedName("direct_source") val directSource: String? = null,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int = 0
)

data class XtreamVodStream(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String = "",
    @SerializedName("rating") val rating: String = "",
    @SerializedName("rating_5based") val rating5based: Double = 0.0,
    @SerializedName("added") val added: String? = null,
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("container_extension") val containerExtension: String = "mp4",
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("direct_source") val directSource: String? = null
)

data class XtreamSeries(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("series_id") val seriesId: Int = 0,
    @SerializedName("cover") val cover: String = "",
    @SerializedName("plot") val plot: String = "",
    @SerializedName("cast") val cast: String = "",
    @SerializedName("director") val director: String = "",
    @SerializedName("genre") val genre: String = "",
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("last_modified") val lastModified: String? = null,
    @SerializedName("rating") val rating: String = "",
    @SerializedName("rating_5based") val rating5based: Double = 0.0,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("youtube_trailer") val youtubeTrailer: String? = null,
    @SerializedName("episode_run_time") val episodeRunTime: String? = null,
    @SerializedName("category_id") val categoryId: String = ""
)

data class XtreamSeriesInfo(
    @SerializedName("seasons") val seasons: List<XtreamSeason>? = null,
    @SerializedName("info") val info: XtreamSeriesDetail? = null,
    @SerializedName("episodes") val episodes: Map<String, List<XtreamEpisode>>? = null
)

data class XtreamSeason(
    @SerializedName("air_date") val airDate: String? = null,
    @SerializedName("episode_count") val episodeCount: Int = 0,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("overview") val overview: String = "",
    @SerializedName("season_number") val seasonNumber: Int = 0,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("cover_big") val coverBig: String? = null
)

data class XtreamSeriesDetail(
    @SerializedName("name") val name: String = "",
    @SerializedName("cover") val cover: String = "",
    @SerializedName("plot") val plot: String = "",
    @SerializedName("cast") val cast: String = "",
    @SerializedName("director") val director: String = "",
    @SerializedName("genre") val genre: String = "",
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("rating") val rating: String = "",
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null
)

data class XtreamEpisode(
    @SerializedName("id") val id: String = "",
    @SerializedName("episode_num") val episodeNum: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("container_extension") val containerExtension: String = "mp4",
    @SerializedName("info") val info: XtreamEpisodeInfo? = null,
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("season") val season: Int = 0,
    @SerializedName("direct_source") val directSource: String? = null
)

data class XtreamEpisodeInfo(
    @SerializedName("tmdb_id") val tmdbId: Int? = null,
    @SerializedName("releasedate") val releaseDate: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("duration_secs") val durationSecs: Int? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("movie_image") val movieImage: String? = null,
    @SerializedName("bitrate") val bitrate: Int? = null,
    @SerializedName("rating") val rating: Double? = null
)

data class XtreamAuthResponse(
    @SerializedName("user_info") val userInfo: XtreamUserInfo? = null,
    @SerializedName("server_info") val serverInfo: XtreamServerInfo? = null
)

data class XtreamUserInfo(
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("message") val message: String = "",
    @SerializedName("auth") val auth: Int = 0,
    @SerializedName("status") val status: String = "",
    @SerializedName("exp_date") val expDate: String? = null,
    @SerializedName("is_trial") val isTrial: String = "",
    @SerializedName("active_cons") val activeCons: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("max_connections") val maxConnections: String = "",
    @SerializedName("allowed_output_formats") val allowedOutputFormats: List<String>? = null
)

data class XtreamServerInfo(
    @SerializedName("url") val url: String = "",
    @SerializedName("port") val port: String = "",
    @SerializedName("https_port") val httpsPort: String = "",
    @SerializedName("server_protocol") val serverProtocol: String = "",
    @SerializedName("rtmp_port") val rtmpPort: String = "",
    @SerializedName("timezone") val timezone: String = "",
    @SerializedName("timestamp_now") val timestampNow: Int = 0,
    @SerializedName("time_now") val timeNow: String = ""
)
