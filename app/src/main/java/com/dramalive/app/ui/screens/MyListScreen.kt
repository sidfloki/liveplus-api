package com.dramalive.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dramalive.app.models.MediaItem
import com.dramalive.app.ui.theme.*

@Composable
fun MyListScreen(
    favorites: List<MediaItem>,
    history: List<MediaItem>,
    onMediaClick: (MediaItem) -> Unit,
    onRemoveFavorite: (MediaItem) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 for Favorites, 1 for History

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
        Text(
            text = "قائمتي",
            color = PureWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
        )

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DeepBlack,
            contentColor = Color(0xFFFF9800),
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFFF9800)
                    )
                }
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("المفضلة", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("شوهد مؤخراً", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        val displayList = if (selectedTab == 0) favorites else history

        if (displayList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (selectedTab == 0) Icons.Rounded.FavoriteBorder else Icons.Rounded.History,
                        contentDescription = null,
                        tint = MutedGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (selectedTab == 0) "لم تقم بإضافة أي محتوى للمفضلة بعد" else "لا يوجد سجل مشاهدة حالياً",
                        color = MutedGray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayList) { item ->
                    FavoriteItemCard(
                        item = item,
                        showRemove = selectedTab == 0,
                        onClick = { onMediaClick(item) },
                        onRemove = { onRemoveFavorite(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: MediaItem,
    showRemove: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = PureWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = when(item.category) {
                    "Movie" -> "فيلم"
                    "Series" -> "مسلسل"
                    else -> "قناة مباشرة"
                },
                color = SubtextGray,
                fontSize = 12.sp
            )
        }
        if (showRemove) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Rounded.Favorite, tint = NetflixRed, contentDescription = "Remove")
            }
        }
        Icon(Icons.Rounded.PlayCircleOutline, tint = PureWhite, contentDescription = "Play")
    }
}
