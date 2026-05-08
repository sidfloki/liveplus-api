package com.dramalive.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dramalive.app.models.MediaItem
import com.dramalive.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    movies: List<MediaItem>,
    series: List<MediaItem>,
    liveChannels: List<MediaItem>,
    isLoading: Boolean,
    onMediaClick: (MediaItem) -> Unit,
    userName: String? = null,
    userPhoto: String? = null,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    val featuredItems = remember(movies, series) {
        (movies.take(5) + series.take(5)).shuffled().take(5)
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        if (isLoading) {
            // Loading State
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = NetflixRed,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("جاري التحميل...", color = SubtextGray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Header with Logo
                item {
                    NetflixHeader(
                        userName = userName, 
                        userPhoto = userPhoto,
                        onSearch = { 
                            searchQuery = it
                            onSearch(it)
                        }
                    )
                }

                // Hero Banner / Featured Carousel
                item {
                    if (featuredItems.isNotEmpty()) {
                        HeroBanner(items = featuredItems, onItemClick = onMediaClick)
                    }
                }

                // Trending Movies
                if (movies.isNotEmpty()) {
                    item {
                        ContentRow(
                            title = "🔥 أفلام رائجة",
                            items = movies.take(20),
                            onItemClick = onMediaClick
                        )
                    }
                }

                // Popular Series
                if (series.isNotEmpty()) {
                    item {
                        ContentRow(
                            title = "📺 مسلسلات شائعة",
                            items = series.take(20),
                            onItemClick = onMediaClick
                        )
                    }
                }

                // Live Channels
                if (liveChannels.isNotEmpty()) {
                    item {
                        ContentRow(
                            title = "📡 قنوات مباشرة",
                            items = liveChannels.take(20),
                            onItemClick = onMediaClick,
                            isLive = true
                        )
                    }
                }

                // Top 10 Movies
                if (movies.size > 5) {
                    item {
                        Top10Row(
                            title = "🏆 أفضل 10 أفلام",
                            items = movies.take(10),
                            onItemClick = onMediaClick
                        )
                    }
                }

                // New Series
                if (series.size > 10) {
                    item {
                        ContentRow(
                            title = "✨ مسلسلات جديدة",
                            items = series.drop(10).take(20),
                            onItemClick = onMediaClick
                        )
                    }
                }

                // Action Movies
                if (movies.size > 20) {
                    item {
                        ContentRow(
                            title = "💥 أفلام أكشن",
                            items = movies.drop(20).take(20),
                            onItemClick = onMediaClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetflixHeader(
    userName: String? = null, 
    userPhoto: String? = null,
    onSearch: (String) -> Unit
) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(DeepBlack)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // App Logo
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.dramalive.app.R.drawable.app_icon),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "LIVE PLUS",
                        color = NetflixRed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    if (userName != null) {
                        Text(
                            text = "مرحباً، $userName",
                            color = SubtextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { isSearchExpanded = !isSearchExpanded }) {
                    Icon(
                        if (isSearchExpanded) Icons.Rounded.Close else Icons.Rounded.Search,
                        contentDescription = "Search", 
                        tint = PureWhite, 
                        modifier = Modifier.size(26.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Rounded.Notifications, contentDescription = "Notifications", tint = PureWhite, modifier = Modifier.size(26.dp))
                }
                if (userPhoto != null) {
                    AsyncImage(
                        model = userPhoto,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NetflixRed, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Expanded Search Bar
        AnimatedVisibility(
            visible = isSearchExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    onSearch(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                placeholder = { Text("ابحث عن أفلام، مسلسلات، قنوات...", color = SubtextGray, fontSize = 14.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NetflixRed,
                    unfocusedBorderColor = DimGray,
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    unfocusedContainerColor = CardDark,
                    focusedContainerColor = CardDark
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchText = ""
                            onSearch("")
                        }) {
                            Icon(Icons.Rounded.Clear, contentDescription = null, tint = SubtextGray)
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroBanner(items: List<MediaItem>, onItemClick: (MediaItem) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { items.size })
    
    // Auto-scroll
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.height(420.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = items[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemClick(item) }
            ) {
                // Background Image
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient overlays
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DeepBlack.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    DeepBlack.copy(alpha = 0.9f),
                                    DeepBlack
                                ),
                                startY = 0f,
                                endY = 1200f
                            )
                        )
                )
                
                // Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    // Category badge
                    Surface(
                        color = NetflixRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = when(item.category) {
                                "Movie" -> "فيلم"
                                "Series" -> "مسلسل"
                                else -> "مباشر"
                            },
                            color = PureWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = item.title,
                        color = PureWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 32.sp
                    )
                    
                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            color = SubtextGray,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Play Button
                        Button(
                            onClick = { onItemClick(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = PureWhite),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = DeepBlack, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تشغيل", color = DeepBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        
                        // Info Button
                        OutlinedButton(
                            onClick = { onItemClick(item) },
                            shape = RoundedCornerShape(6.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(SubtextGray, SubtextGray))
                            ),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("معلومات", color = PureWhite, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Pager Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(items.size) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == pagerState.currentPage) 20.dp else 6.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (index == pagerState.currentPage) NetflixRed else SubtextGray.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
fun ContentRow(
    title: String,
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    isLive: Boolean = false
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = title,
            color = PureWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { item ->
                NetflixCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    isLive = isLive
                )
            }
        }
    }
}

@Composable
fun NetflixCard(
    item: MediaItem,
    onClick: () -> Unit,
    isLive: Boolean = false
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .height(195.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CardDark)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Live badge
            if (isLive) {
                Surface(
                    color = NetflixRed,
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(PureWhite)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LIVE", color = PureWhite, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // Rating badge
            if (item.rating.isNotEmpty() && item.rating != "0") {
                Surface(
                    color = AccentGold.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = DeepBlack, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = item.rating.take(3),
                            color = DeepBlack,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Play icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = PureWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = when(item.category) {
                "Movie" -> "فيلم"
                "Series" -> "مسلسل"
                else -> "مباشر"
            },
            color = SubtextGray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun Top10Row(
    title: String,
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = title,
            color = PureWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items.size) { index ->
                val item = items[index]
                Row(
                    modifier = Modifier
                        .clickable { onItemClick(item) }
                        .height(180.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Big number
                    Text(
                        text = "${index + 1}",
                        color = PureWhite.copy(alpha = 0.15f),
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 120.sp
                    )

                    // Card
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(170.dp)
                            .offset(x = (-20).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardDark)
                    ) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                                    )
                                )
                        )

                        Text(
                            text = item.title,
                            color = PureWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
