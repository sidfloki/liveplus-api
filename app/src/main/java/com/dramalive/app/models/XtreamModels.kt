package com.dramalive.app.models

import com.google.gson.annotations.SerializedName

data class XtreamAuthResponse(
    @SerializedName("user_info") val userInfo: XtreamUserInfo?,
    @SerializedName("server_info") val serverInfo: XtreamServerInfo?
)

data class XtreamUserInfo(
    val status: String?,
    val exp_date: String?,
    val auth: Int?
)

data class XtreamServerInfo(
    val url: String?,
    val port: String?,
    val https_port: String?,
    val server_protocol: String?,
    val timezone: String?
)

data class XtreamCategory(
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("parent_id") val parentId: Int
)

data class XtreamLiveStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String,
    @SerializedName("category_id") val categoryId: String
)

data class XtreamVodStream(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val streamIcon: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("container_extension") val containerExtension: String
)

data class XtreamSeries(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("series_id") val seriesId: Int,
    @SerializedName("cover") val cover: String,
    @SerializedName("plot") val plot: String,
    @SerializedName("cast") val cast: String,
    @SerializedName("director") val director: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("rating") val rating: String,
    @SerializedName("category_id") val categoryId: String
)

data class XtreamSeriesInfo(
    @SerializedName("seasons") val seasons: List<XtreamSeason>?,
    @SerializedName("episodes") val episodes: Map<String, List<XtreamEpisode>>?
)

data class XtreamSeason(
    @SerializedName("air_date") val airDate: String?,
    @SerializedName("episode_count") val episodeCount: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("cover") val cover: String?
)

data class XtreamEpisode(
    @SerializedName("id") val id: String,
    @SerializedName("episode_num") val episodeNum: Int,
    @SerializedName("title") val title: String,
    @SerializedName("container_extension") val containerExtension: String,
    @SerializedName("info") val info: XtreamEpisodeInfo?,
    @SerializedName("season") val season: Int
)

data class XtreamVodInfo(
    @SerializedName("info") val info: XtreamMovieData?,
    @SerializedName("subtitles") val subtitles: List<XtreamSubtitle>?
)

data class XtreamMovieData(
    @SerializedName("movie_image") val movieImage: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("releasedate") val releaseDate: String?
)

data class XtreamSubtitle(
    @SerializedName("id") val id: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("extension") val extension: String?,
    @SerializedName("url") val url: String?
)

data class XtreamEpisodeInfo(
    @SerializedName("movie_image") val movieImage: String?,
    @SerializedName("duration") val duration: String?,
    @SerializedName("plot") val plot: String?,
    @SerializedName("subtitles") val subtitles: List<XtreamSubtitle>?
)
