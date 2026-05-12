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

@Composable
fun HomeScreen(
    liveChannels: List<MediaItem>,
    isLoading: Boolean,
    onCategoryClick: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    val categories = remember(liveChannels) {
        liveChannels.map { it.category }.distinct().filter { it.isNotEmpty() }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFF9800)) // VLC Orange
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 40.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "أقسام القنوات",
                        color = PureWhite,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(categories) { category ->
                    CategorySection(
                        name = category,
                        count = liveChannels.count { it.category == category },
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySection(name: String, count: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Pattern or Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF9800).copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    color = PureWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count قناة",
                    color = SubtextGray,
                    fontSize = 14.sp
                )
            }

            // VLC-style Icon at the end
            Icon(
                imageVector = Icons.Rounded.PlayCircle,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
                    .size(48.dp)
            )
        }
    }
}
