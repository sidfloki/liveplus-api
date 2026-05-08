package com.dramalive.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.dramalive.app.api.XtreamRepository
import com.dramalive.app.models.*
import com.dramalive.app.ui.theme.*
import com.dramalive.app.util.ExternalPlayerLauncher
import com.dramalive.app.util.FavoritesManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME("الرئيسية", Icons.Rounded.Home, Icons.Filled.Home),
    MOVIES("أفلام", Icons.Rounded.Movie, Icons.Filled.Movie),
    SERIES("مسلسلات", Icons.Rounded.Tv, Icons.Filled.Tv),
    CHANNELS("قنوات", Icons.Rounded.LiveTv, Icons.Filled.LiveTv),
    MY_LIST("قائمتي", Icons.Rounded.FavoriteBorder, Icons.Rounded.Favorite)
}

@Composable
fun DramaLiveScreen(
    userName: String? = null,
    userPhoto: String? = null
) {
    val repository = remember { XtreamRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Navigation state
    var currentTab by remember { mutableStateOf(BottomNavItem.HOME) }
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    var selectedSeries by remember { mutableStateOf<MediaItem?>(null) }

    // Data for series details
    var seasons by remember { mutableStateOf<List<XtreamSeason>>(emptyList()) }
    var episodes by remember { mutableStateOf<List<XtreamEpisode>>(emptyList()) }
    var isLoadingSeriesDetails by remember { mutableStateOf(false) }

    // Data states
    var movies by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var series by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var liveChannels by remember { mutableStateOf<List<MediaItem>>(emptyList()) }

    var movieCategories by remember { mutableStateOf<List<XtreamCategory>>(emptyList()) }
    var seriesCategories by remember { mutableStateOf<List<XtreamCategory>>(emptyList()) }
    var liveCategories by remember { mutableStateOf<List<XtreamCategory>>(emptyList()) }

    var allMovies by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var allSeries by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var allChannels by remember { mutableStateOf<List<MediaItem>>(emptyList()) }

    var isLoadingHome by remember { mutableStateOf(true) }
    var isLoadingMovies by remember { mutableStateOf(false) }
    var isLoadingSeries by remember { mutableStateOf(false) }
    var isLoadingChannels by remember { mutableStateOf(false) }

    // Favorites and History state
    var favoriteItems by remember { mutableStateOf(FavoritesManager.getFavorites(context)) }
    var watchHistory by remember { mutableStateOf(FavoritesManager.getHistory(context)) }

    fun refreshFavorites() {
        favoriteItems = FavoritesManager.getFavorites(context)
        watchHistory = FavoritesManager.getHistory(context)
    }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Unwanted category keywords
    val unwantedKeywords = listOf(
        "customers reviews", "telegeram", "telegram", "new channel", 
        "جدول المباريات", "تواصل معنا", "اعلانات سيرفر", "تواصل", "reviews"
    )

    fun filterCategories(cats: List<XtreamCategory>): List<XtreamCategory> {
        return cats.filter { cat ->
            val nameLower = cat.categoryName.lowercase()
            unwantedKeywords.none { nameLower.contains(it) }
        }
    }

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    // Load data on launch
    LaunchedEffect(Unit) {
        isLoadingHome = true

        // Load Movies
        scope.launch {
            try {
                val vodResult = repository.getVodStreams()
                vodResult.onSuccess { vodList ->
                    // Limit initial load to 1000 items for performance
                    val mediaItems = vodList.take(1000).map { repository.vodToMediaItem(it) }
                    movies = mediaItems
                    allMovies = mediaItems
                }
                val catResult = repository.getVodCategories()
                catResult.onSuccess { cats ->
                    movieCategories = filterCategories(cats)
                }
            } catch (e: Exception) { 
                android.util.Log.e("DramaLive", "Error loading movies: ${e.message}")
            }
        }

        // Load Series
        scope.launch {
            try {
                val seriesResult = repository.getSeries()
                seriesResult.onSuccess { seriesList ->
                    // Limit initial load to 1000 items for performance
                    val mediaItems = seriesList.take(1000).map { repository.seriesToMediaItem(it) }
                    series = mediaItems
                    allSeries = mediaItems
                }
                val catResult = repository.getSeriesCategories()
                catResult.onSuccess { cats ->
                    seriesCategories = filterCategories(cats)
                }
            } catch (e: Exception) {
                android.util.Log.e("DramaLive", "Error loading series: ${e.message}")
            }
        }

        // Load Live Channels
        val liveJob = scope.launch {
            try {
                val liveResult = repository.getLiveStreams()
                liveResult.onSuccess { liveList ->
                    val mediaItems = liveList.map { repository.liveStreamToMediaItem(it) }
                    liveChannels = mediaItems
                    allChannels = mediaItems
                }
                val catResult = repository.getLiveCategories()
                catResult.onSuccess { cats ->
                    liveCategories = filterCategories(cats)
                }
            } catch (_: Exception) { }
        }

        // Wait for all to finish
        scope.launch {
            liveJob.join()
            isLoadingHome = false
        }
    }

    // Launch external player when media is selected
    LaunchedEffect(selectedMedia) {
        selectedMedia?.let { media ->
            if (media.videoUrl.isNotEmpty()) {
                ExternalPlayerLauncher.launch(
                    context = context,
                    url = media.videoUrl,
                    title = media.title
                )
                FavoritesManager.addToHistory(context, media)
                refreshFavorites()
            }
            // Reset after launching
            selectedMedia = null
        }
    }

    // Main Layout
    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        // Content
        when (currentTab) {
            BottomNavItem.HOME -> {
                HomeScreen(
                    movies = movies.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    series = series.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    liveChannels = liveChannels.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    isLoading = isLoadingHome,
                    onMediaClick = { media ->
                        if (media.category == "Series") {
                            selectedSeries = media
                            isLoadingSeriesDetails = true
                            scope.launch {
                                val result = repository.getSeriesInfo(media.id.toString())
                                result.onSuccess { info ->
                                    seasons = info.seasons ?: emptyList()
                                    episodes = info.episodes?.values?.flatten() ?: emptyList()
                                }
                                isLoadingSeriesDetails = false
                            }
                        } else if (media.videoUrl.isNotEmpty()) {
                            ExternalPlayerLauncher.launch(
                                context = context,
                                url = media.videoUrl,
                                title = media.title
                            )
                        }
                    },
                    userName = userName,
                    userPhoto = userPhoto,
                    onSearch = { searchQuery = it }
                )
            }
            BottomNavItem.MOVIES -> {
                MoviesScreen(
                    movies = movies.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    categories = movieCategories,
                    isLoading = isLoadingMovies,
                    onMovieClick = { movie ->
                        if (movie.videoUrl.isNotEmpty()) {
                            ExternalPlayerLauncher.launch(
                                context = context,
                                url = movie.videoUrl,
                                title = movie.title
                            )
                        }
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onSearch = { searchQuery = it }
                )
            }
            BottomNavItem.SERIES -> {
                SeriesScreen(
                    series = series.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    categories = seriesCategories,
                    isLoading = isLoadingSeries,
                    onSeriesClick = { seriesItem ->
                        selectedSeries = seriesItem
                        isLoadingSeriesDetails = true
                        scope.launch {
                            val result = repository.getSeriesInfo(seriesItem.id.toString())
                            result.onSuccess { info ->
                                seasons = info.seasons ?: emptyList()
                                episodes = info.episodes?.values?.flatten() ?: emptyList()
                            }
                            isLoadingSeriesDetails = false
                        }
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onSearch = { searchQuery = it }
                )
            }
            BottomNavItem.CHANNELS -> {
                ChannelsScreen(
                    channels = liveChannels.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    categories = liveCategories,
                    isLoading = isLoadingChannels,
                    onChannelClick = { channel ->
                        if (channel.videoUrl.isNotEmpty()) {
                            ExternalPlayerLauncher.launch(
                                context = context,
                                url = channel.videoUrl,
                                title = channel.title
                            )
                            FavoritesManager.addToHistory(context, channel)
                            refreshFavorites()
                        }
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onSearch = { searchQuery = it }
                )
            }
            BottomNavItem.MY_LIST -> {
                MyListScreen(
                    favorites = favoriteItems,
                    history = watchHistory,
                    onMediaClick = { media ->
                        ExternalPlayerLauncher.launch(context, media.videoUrl, media.title)
                        FavoritesManager.addToHistory(context, media)
                        refreshFavorites()
                    },
                    onRemoveFavorite = { item ->
                        FavoritesManager.toggleFavorite(context, item)
                        refreshFavorites()
                    }
                )
            }
        }

        // Series Details overlay (Now placed as a top layer)
        if (selectedSeries != null) {
            BackHandler { selectedSeries = null }
            SeriesDetailsScreen(
                series = selectedSeries!!,
                seasons = seasons,
                episodes = episodes,
                isLoading = isLoadingSeriesDetails,
                isFavorite = FavoritesManager.isFavorite(context, selectedSeries!!.id),
                onToggleFavorite = {
                    FavoritesManager.toggleFavorite(context, selectedSeries!!)
                    refreshFavorites()
                },
                onEpisodeClick = { episode ->
                    val episodeUrl = com.dramalive.app.Config.getSeriesUrl(episode.id, episode.containerExtension)
                    ExternalPlayerLauncher.launch(
                        context = context,
                        url = episodeUrl,
                        title = episode.title
                    )
                    FavoritesManager.addToHistory(context, selectedSeries!!)
                    refreshFavorites()
                },
                onBack = { selectedSeries = null }
            )
        }

        // Verification Reminder
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !user.isEmailVerified) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp, start = 20.dp, end = 20.dp),
                color = NetflixRed.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "يرجى تفعيل بريدك الإلكتروني للحصول على كافة الميزات.",
                        color = PureWhite,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { 
                        user.sendEmailVerification()
                        Toast.makeText(context, "تم إعادة إرسال رابط التفعيل", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("إعادة إرسال", color = PureWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bottom UI: Nav Bar + Ad Banner
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Gradient fade above nav bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, DeepBlack.copy(alpha = 0.95f))
                        )
                    )
            )

            NavigationBar(
                containerColor = DeepBlack.copy(alpha = 0.97f),
                tonalElevation = 0.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                BottomNavItem.entries.forEach { item ->
                    val isSelected = currentTab == item
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = item },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NetflixRed,
                            selectedTextColor = NetflixRed,
                            unselectedIconColor = MutedGray,
                            unselectedTextColor = MutedGray,
                            indicatorColor = NetflixRed.copy(alpha = 0.15f)
                        )
                    )
                }
            }
            
            // Add Ad Banner at the very bottom
            com.dramalive.app.ui.components.AdBanner()
        }
    }
}