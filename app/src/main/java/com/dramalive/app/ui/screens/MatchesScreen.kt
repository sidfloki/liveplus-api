package com.dramalive.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dramalive.app.ui.theme.*
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.net.URL

@Composable
fun MatchesScreen() {
    var matches by remember { mutableStateOf<List<Match>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            delay(1500)
            matches = listOf(
                Match("Real Madrid", "Barcelona", "21:00", "La Liga", "Upcoming"),
                Match("Man City", "Liverpool", "18:30", "Premier League", "LIVE"),
                Match("Bayern Munich", "Dortmund", "19:45", "Bundesliga", "Upcoming"),
                Match("AC Milan", "Inter Milan", "22:00", "Serie A", "Finished"),
                Match("PSG", "Marseille", "20:00", "Ligue 1", "Upcoming")
            )
        } catch (e: Exception) {
            // Handle error
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "MATCHES",
                color = PureWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NetflixRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches) { match ->
                    MatchCard(match)
                }
            }
        }
    }
}

@Composable
fun MatchCard(match: Match) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        color = CardDark,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222222))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(match.league, color = SubtextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                
                Surface(
                    color = if (match.status == "LIVE") Color(0xFFE50914).copy(alpha = alpha) else Color.DarkGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = match.status,
                        color = PureWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Team A
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.White.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(match.teamA.take(1), color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(match.teamA, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                }

                // Time / VS
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = match.time, 
                        color = if (match.status == "LIVE") Color(0xFFFF9800) else PureWhite, 
                        fontWeight = FontWeight.Black, 
                        fontSize = 22.sp
                    )
                    Text("VS", color = SubtextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Team B
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.White.copy(alpha = 0.05f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(match.teamB.take(1), color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(match.teamB, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { /* Navigate to channel if live */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (match.status == "LIVE") Color(0xFFFF9800) else Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                border = if (match.status == "LIVE") null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
            ) {
                Text(
                    text = if (match.status == "LIVE") "Watch Live" else "Remind Me", 
                    color = if (match.status == "LIVE") Color.Black else PureWhite, 
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class Match(
    val teamA: String,
    val teamB: String,
    val time: String,
    val league: String,
    val status: String
)
