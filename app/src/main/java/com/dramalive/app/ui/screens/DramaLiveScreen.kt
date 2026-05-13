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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    MOVIES("FILM", Icons.Rounded.Movie, Icons.Filled.Movie),
    SERIES("SERIE", Icons.Rounded.Tv, Icons.Filled.Tv),
    CHANNELS("TV", Icons.Rounded.LiveTv, Icons.Filled.LiveTv),
    MATCHES("EVENT", Icons.Rounded.EmojiEvents, Icons.Filled.EmojiEvents),
    ACCOUNT("ACCOUNT", Icons.Rounded.Person, Icons.Filled.Person)
}

@Composable
fun DramaLiveScreen(
    userName: String,
    userPhoto: String?,
    onLoginRequest: () -> Unit
) {
    val repository = remember { XtreamRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Navigation state
    var currentTab by remember { mutableStateOf(BottomNavItem.MOVIES) }
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
        "matches schedule", "contact us", "server ads", "contact", "reviews"
    )

    fun filterCategories(cats: List<XtreamCategory>): List<XtreamCategory> {
        return cats.filter { cat ->
            val nameLower = cat.categoryName.lowercase()
            unwantedKeywords.none { nameLower.contains(it) }
        }
    }

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Load data on launch
    val directLinks by com.dramalive.app.util.DirectLinksStore.directLinks.collectAsState()

    LaunchedEffect(Unit) {
        isLoadingHome = true
        isLoadingMovies = true
        isLoadingSeries = true
        isLoadingChannels = true

        // Load Movies
        val movieJob = scope.launch {
            try {
                val movieResult = repository.getVodStreams()
                movieResult.onSuccess { vodList ->
                    val mediaItems = vodList.map { repository.vodToMediaItem(it) }
                    movies = mediaItems
                    allMovies = mediaItems
                }
                val catResult = repository.getVodCategories()
                catResult.onSuccess { cats ->
                    movieCategories = filterCategories(cats)
                }
            } catch (_: Exception) { }
            isLoadingMovies = false
        }

        // Load Series
        val seriesJob = scope.launch {
            try {
                val seriesResult = repository.getSeries()
                seriesResult.onSuccess { seriesList ->
                    val mediaItems = seriesList.map { repository.seriesToMediaItem(it) }
                    series = mediaItems
                    allSeries = mediaItems
                }
                val catResult = repository.getSeriesCategories()
                catResult.onSuccess { cats ->
                    seriesCategories = filterCategories(cats)
                }
            } catch (_: Exception) { }
            isLoadingSeries = false
        }

        // Load Live Channels
        val liveJob = scope.launch {
            try {
                val liveResult = repository.getLiveStreams()
                liveResult.onSuccess { liveList ->
                    val mediaItems = liveList.map { repository.liveStreamToMediaItem(it) }
                    liveChannels = mediaItems
                }
                
                // Fetch extra channels from GitHub M3U
                try {
                    val extraChannels = com.dramalive.app.util.M3UParser.fetchFromUrl(com.dramalive.app.Config.REMOTE_M3U_URL)
                    liveChannels = liveChannels + extraChannels
                } catch (e: Exception) {
                    android.util.Log.e("DramaLive", "Failed to load M3U: ${e.message}")
                }
                
                allChannels = liveChannels
                
                val catResult = repository.getLiveCategories()
                catResult.onSuccess { cats ->
                    liveCategories = filterCategories(cats)
                }
            } catch (_: Exception) { }
            isLoadingChannels = false
        }

        // Wait for all to finish
        scope.launch {
            movieJob.join()
            seriesJob.join()
            liveJob.join()
            isLoadingHome = false
        }
    }

    // Launch external player when media is selected
    LaunchedEffect(selectedMedia) {
        selectedMedia?.let { media ->
                // Track Media Play
                val bundle = android.os.Bundle()
                bundle.putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_ID, media.id.toString())
                bundle.putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME, media.title)
                bundle.putString(com.google.firebase.analytics.FirebaseAnalytics.Param.CONTENT_TYPE, media.category)
                Firebase.analytics.logEvent(com.google.firebase.analytics.FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

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

    // Main Layout
    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        // Content
        when (currentTab) {
            BottomNavItem.MOVIES -> {
                MoviesScreen(
                    movies = movies.filter { it.title.contains(searchQuery, ignoreCase = true) },
                    categories = movieCategories,
                    isLoading = isLoadingMovies,
                    onMovieClick = { movie ->
                        scope.launch {
                            val infoResult = repository.getVodInfo(movie.id.toString().toIntOrNull() ?: 0)
                            var subtitleUrl: String? = null
                            infoResult.onSuccess { info ->
                                subtitleUrl = info.subtitles?.find { 
                                    it.language?.lowercase()?.contains("ara") == true || 
                                    it.language?.lowercase()?.contains("arabic") == true 
                                }?.url ?: info.subtitles?.firstOrNull()?.url
                            }
                            (context as? com.dramalive.app.MainActivity)?.showInterstitial {
                                ExternalPlayerLauncher.launch(context, movie.videoUrl, movie.title, subtitleUrl)
                                FavoritesManager.addToHistory(context, movie)
                                refreshFavorites()
                            }
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
                    onSeriesClick = { item ->
                        isLoadingSeriesDetails = true
                        selectedSeries = item
                        scope.launch {
                            val infoResult = repository.getSeriesInfo(item.id.toString())
                            infoResult.onSuccess { info ->
                                seasons = info.seasons ?: emptyList()
                                episodes = info.episodes?.getOrDefault("1", emptyList()) ?: emptyList()
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
                    channels = (directLinks + liveChannels).filter { 
                        it.title.contains(searchQuery, ignoreCase = true) &&
                        (selectedCategory == null || it.category == selectedCategory)
                    },
                    categories = liveCategories,
                    isLoading = isLoadingChannels,
                    onChannelClick = { channel ->
                        if (channel.videoUrl.isNotEmpty()) {
                            // Show Ad before playing
                            (context as? com.dramalive.app.MainActivity)?.showInterstitial {
                                ExternalPlayerLauncher.launch(
                                    context = context,
                                    url = channel.videoUrl,
                                    title = channel.title
                                )
                                FavoritesManager.addToHistory(context, channel)
                                refreshFavorites()
                            }
                        }
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onSearch = { searchQuery = it }
                )
            }
            BottomNavItem.MATCHES -> {
                MatchesScreen()
            }
            BottomNavItem.ACCOUNT -> {
                ProfileScreen(
                    userName = userName ?: "User",
                    userPhoto = userPhoto,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        (context as? com.dramalive.app.MainActivity)?.recreate()
                    },
                    onLoginRequest = onLoginRequest
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
                    val subtitleUrl = episode.info?.subtitles?.find { 
                        it.language?.lowercase()?.contains("ara") == true || 
                        it.language?.lowercase()?.contains("arabic") == true 
                    }?.url ?: episode.info?.subtitles?.firstOrNull()?.url
                    
                    (context as? com.dramalive.app.MainActivity)?.showInterstitial {
                        ExternalPlayerLauncher.launch(
                            context = context,
                            url = episodeUrl,
                            title = episode.title,
                            subtitleUrl = subtitleUrl
                        )
                        FavoritesManager.addToHistory(context, selectedSeries!!)
                        refreshFavorites()
                    }
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
                        "Please verify your email to access all features.",
                        color = PureWhite,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { 
                        user.sendEmailVerification()
                        Toast.makeText(context, "Verification link resent", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Resend", color = PureWhite, fontWeight = FontWeight.Bold)
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
                        onClick = { 
                            currentTab = item
                            if (item == BottomNavItem.CHANNELS) {
                                selectedCategory = null
                            }
                        },
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
                            selectedIconColor = Color(0xFFFF9800),
                            selectedTextColor = Color(0xFFFF9800),
                            unselectedIconColor = MutedGray,
                            unselectedTextColor = MutedGray,
                            indicatorColor = Color(0xFFFF9800).copy(alpha = 0.15f)
                        )
                    )
                }
            }
            
            // Add Ad Banner at the very bottom
            com.dramalive.app.ui.components.AdBanner()
        }
    }
}

@Composable
fun ProfileScreen(
    userName: String,
    userPhoto: String?,
    onLogout: () -> Unit,
    onLoginRequest: () -> Unit
) {
    val isGuest = userName == "User" || userName.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = NetflixRed
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isGuest) "You are browsing as Guest" else "Welcome, $userName",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PureWhite
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isGuest) {
            Button(
                onClick = onLoginRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login / Sign Up", color = PureWhite)
            }
        } else {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = NetflixRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout", color = PureWhite)
            }
        }
    }
}