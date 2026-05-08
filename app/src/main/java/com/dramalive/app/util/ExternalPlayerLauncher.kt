package com.dramalive.app.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object ExternalPlayerLauncher {

    private const val VLC_PACKAGE = "org.videolan.vlc"

    /**
     * يقوم بتشغيل الفيديو في مشغل VLC حصراً
     * إذا لم يكن مثبت، يوجه المستخدم لمتجر Play
     */
    fun launch(context: Context, url: String, title: String = "") {
        if (url.isEmpty()) {
            Toast.makeText(context, "رابط الفيديو غير صالح", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            setPackage(VLC_PACKAGE) // Force VLC
            putExtra("title", title)
            putExtra("from_start", false)
            putExtra("position", 0L)
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
            } catch (e: Exception) {
                // Fallback to chooser if specific launch fails (rare but possible)
                val chooser = Intent.createChooser(intent, "اختر مشغل (يفضل VLC)")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            }
        } else {
            // VLC is NOT installed
            Toast.makeText(context, "يرجى تثبيت مشغل VLC للمتابعة", Toast.LENGTH_LONG).show()
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
