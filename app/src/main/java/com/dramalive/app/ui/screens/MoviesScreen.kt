package com.dramalive.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dramalive.app.models.MediaItem
import com.dramalive.app.models.XtreamCategory
import com.dramalive.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    movies: List<MediaItem>,
    categories: List<XtreamCategory>,
    isLoading: Boolean,
    onMovieClick: (MediaItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onSearch: (String) -> Unit
) {
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Rounded.Menu, contentDescription = "Menu", tint = PureWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("FILM", color = PureWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                    Icon(
                        if (isSearchVisible) Icons.Rounded.Close else Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = PureWhite
                    )
                }
            }

            // Search Bar
            AnimatedVisibility(visible = isSearchVisible) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        onSearch(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    placeholder = { Text("Search movies...", color = SubtextGray) },
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

        // Content
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NetflixRed, strokeWidth = 3.dp)
            }
        } else if (movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.MovieFilter, contentDescription = null, tint = MutedGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No movies found", color = MutedGray, fontSize = 16.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies) { movie ->
                    val context = androidx.compose.ui.platform.LocalContext.current
                    MovieGridCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onDownloadClick = {
                            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            if (user == null) {
                                android.widget.Toast.makeText(context, "Please login to download", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                (context as? com.dramalive.app.MainActivity)?.showInterstitial {
                                    com.dramalive.app.util.MediaDownloadManager.downloadMedia(context, movie)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieGridCard(movie: MediaItem, onClick: () -> Unit, onDownloadClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CardDark)
        ) {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Bottom gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Play icon
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(NetflixRed.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
            }
            
            // Download icon
            IconButton(
                onClick = { onDownloadClick() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Rounded.Download, contentDescription = "Download", tint = PureWhite, modifier = Modifier.size(16.dp))
            }

            // Rating
            if (movie.rating.isNotEmpty() && movie.rating != "0") {
                Surface(
                    color = AccentGold.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = DeepBlack, modifier = Modifier.size(9.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(movie.rating.take(3), color = DeepBlack, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Text(
            text = movie.title,
            color = PureWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp, start = 2.dp)
        )
    }
}
