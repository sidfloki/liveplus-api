package com.dramalive.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                // TEST Ad Unit ID - Replace with your production ID later
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-7876868777201120/2387438883"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
