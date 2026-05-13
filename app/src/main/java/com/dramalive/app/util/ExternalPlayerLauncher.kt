package com.dramalive.app.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object ExternalPlayerLauncher {

    private const val VLC_PACKAGE = "org.videolan.vlc"
    private const val MX_PACKAGE = "com.mxtech.videoplayer.ad"

    fun launch(context: Context, url: String, title: String = "", subtitleUrl: String? = null) {
        if (url.isEmpty()) {
            Toast.makeText(context, "Invalid video URL", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            setPackage(VLC_PACKAGE)
            putExtra("title", title)
            if (!subtitleUrl.isNullOrEmpty()) {
                putExtra("subtitles_location", subtitleUrl)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pm = context.packageManager
        val isVlcInstalled = try {
            pm.getPackageInfo(VLC_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        if (isVlcInstalled) {
            try {
                context.startActivity(intent)
                if (subtitleUrl.isNullOrEmpty()) {
                    Toast.makeText(context, "Tip: You can download Arabic subtitles in VLC menu", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Subtitles loaded automatically", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                val chooser = Intent.createChooser(intent, "Select Player (VLC Recommended)")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } else {
            Toast.makeText(context, "Please install VLC Player to continue", Toast.LENGTH_LONG).show()
            openPlayStore(context, VLC_PACKAGE)
        }
    }

    private fun openPlayStore(context: Context, packageName: String) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
