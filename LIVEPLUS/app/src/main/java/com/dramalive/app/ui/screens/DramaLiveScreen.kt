package com.dramalive.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dramalive.app.data.LocalData
import com.dramalive.app.models.MediaItem
import com.dramalive.app.ui.components.VideoPlayer
import com.google.firebase.auth.FirebaseAuth

// Premium Colors
val DeepBlack = Color(0xFF0F0F0F)
val CardDark = Color(0xFF242424)
val AccentCyan = Color(0xFF00D9FF)
val DeepRoyalBlue = Color(0xFF1E3A8A)

@Composable
fun DramaLiveScreen(
    onGoogleLogin: () -> Unit
) {
    var selectedMedia by remember { mutableStateOf<MediaItem?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }
    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    val categories = listOf("All", "Sports 🏆", "Movies 🎬", "Series 📺", "Events 🎪", "Kids 🧸", "News 📰")

    // Listen for auth state changes
    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            user = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlack)) {
        
        // Fixed Top Area (Header or Player)
        if (selectedMedia != null) {
            BackHandler { selectedMedia = null }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                VideoPlayer(
                    videoUrl = selectedMedia!!.videoUrl,
                    onClose = { selectedMedia = null }
                )
            }
        } else {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (user != null) "Hello, ${user.displayName?.split(" ")?.firstOrNull() ?: "User"}" else "Welcome",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Watch your favorite channels",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                
                if (user == null) {
                    IconButton(onClick = onGoogleLogin) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Login", tint = AccentCyan, modifier = Modifier.size(32.dp))
                    }
                } else {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = "User",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, AccentCyan, CircleShape)
                    )
                }
            }
        }

        // Categories Bar (Always Horizontal under player or header)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        // Scrollable Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SectionHeader("Trending Now")
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(LocalData.movies) { movie ->
                        MediaCard(movie) { selectedMedia = movie }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                SectionHeader("Popular Series")
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(LocalData.series) { serie ->
                        MediaCard(serie) { selectedMedia = serie }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            item {
                SectionHeader("Recommended for You")
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(LocalData.movies.shuffled()) { movie ->
                        MediaCard(movie) { selectedMedia = movie }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        color = if (isSelected) AccentCyan else CardDark,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun MediaCard(item: MediaItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(end = 12.dp)
            .clickable { onClick() }
    ) {
        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .height(210.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardDark),
                contentScale = ContentScale.Crop
            )
            // Premium Overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            startY = 300f
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Full HD",
            color = AccentCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}