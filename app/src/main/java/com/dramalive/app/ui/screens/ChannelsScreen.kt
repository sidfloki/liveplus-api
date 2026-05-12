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
fun ChannelsScreen(
    channels: List<MediaItem>,
    categories: List<XtreamCategory>,
    isLoading: Boolean,
    onChannelClick: (MediaItem) -> Unit,
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
                Text("قنوات مباشرة", color = PureWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
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
                    placeholder = { Text("ابحث عن قنوات...", color = SubtextGray) },
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
        } else if (channels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SatelliteAlt, contentDescription = null, tint = MutedGray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("لا توجد قنوات", color = MutedGray, fontSize = 16.sp)
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
                items(channels) { channel ->
                    ChannelCard(channel = channel, onClick = { onChannelClick(channel) })
                }
            }
        }
    }
}

@Composable
fun ChannelCard(channel: MediaItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CardDark, CardHover)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = channel.imageUrl,
                contentDescription = channel.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            // Live badge
            Surface(
                color = Color(0xFFFF9800),
                shape = RoundedCornerShape(3.dp),
                modifier = Modifier
                    .padding(6.dp)
                    .align(Alignment.TopEnd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(PureWhite)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("LIVE", color = PureWhite, fontSize = 7.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = channel.title,
            color = PureWhite,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}
