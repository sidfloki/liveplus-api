package com.dramalive.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dramalive.app.models.MediaItem
import com.dramalive.app.models.XtreamEpisode
import com.dramalive.app.models.XtreamSeason
import com.dramalive.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailsScreen(
    series: MediaItem,
    seasons: List<XtreamSeason>,
    episodes: List<XtreamEpisode>,
    isLoading: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onEpisodeClick: (XtreamEpisode) -> Unit,
    onBack: () -> Unit
) {
    var selectedSeason by remember { mutableIntStateOf(1) }
    val filteredEpisodes = episodes.filter { it.season == selectedSeason }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Header Image
            item {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    AsyncImage(
                        model = series.imageUrl,
                        contentDescription = series.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DeepBlack)
                                )
                            )
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null, tint = PureWhite)
                    }
                }
            }

            // Series Info
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = series.title,
                        color = PureWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(series.year, color = SubtextGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            color = DimGray,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "HD",
                                color = PureWhite,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        if (series.rating.isNotEmpty()) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Text(series.rating, color = PureWhite, fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = onToggleFavorite,
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFavorite) NetflixRed else Color.DarkGray),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isFavorite) "في قائمتي" else "إضافة لقائمتي")
                        }
                    }

                    Text(
                        text = series.description,
                        color = SubtextGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // Seasons Selector
            if (seasons.isNotEmpty()) {
                item {
                    ScrollableTabRow(
                        selectedTabIndex = (selectedSeason - 1).coerceAtLeast(0),
                        containerColor = Color.Transparent,
                        contentColor = NetflixRed,
                        edgePadding = 16.dp,
                        divider = {}
                    ) {
                        seasons.forEach { season ->
                            Tab(
                                selected = selectedSeason == season.seasonNumber,
                                onClick = { selectedSeason = season.seasonNumber },
                                text = {
                                    Text(
                                        text = "الموسم ${season.seasonNumber}",
                                        color = if (selectedSeason == season.seasonNumber) PureWhite else SubtextGray,
                                        fontWeight = if (selectedSeason == season.seasonNumber) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Episodes List
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NetflixRed)
                    }
                }
            } else {
                items(filteredEpisodes) { episode ->
                    EpisodeItem(
                        episode = episode, 
                        onClick = { onEpisodeClick(episode) },
                        onDownload = {
                            val episodeUrl = com.dramalive.app.Config.getSeriesUrl(episode.id, episode.containerExtension)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(episodeUrl))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(episode: XtreamEpisode, onClick: () -> Unit, onDownload: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardDark)
        ) {
            AsyncImage(
                model = episode.info?.movieImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = PureWhite.copy(alpha = 0.8f), modifier = Modifier.size(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${episode.episodeNum}. ${episode.title}",
                color = PureWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = episode.info?.duration ?: "",
                color = SubtextGray,
                fontSize = 12.sp
            )
        }
        
        IconButton(onClick = onDownload) {
            Icon(Icons.Rounded.Download, contentDescription = null, tint = SubtextGray)
        }
    }
}
