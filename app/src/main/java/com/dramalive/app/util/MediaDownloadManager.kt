package com.dramalive.app.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.dramalive.app.models.MediaItem

object MediaDownloadManager {

    fun downloadMedia(context: Context, item: MediaItem) {
        if (item.videoUrl.isEmpty()) {
            Toast.makeText(context, "رابط الفيديو غير صالح", Toast.LENGTH_SHORT).show()
            return
        }

        val request = DownloadManager.Request(Uri.parse(item.videoUrl))
            .setTitle("جاري تحميل: ${item.title}")
            .setDescription("تحميل الفيلم للمشاهدة بدون إنترنت")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "LivePlus/${item.title}.mp4")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        
        Toast.makeText(context, "بدأ التحميل...", Toast.LENGTH_SHORT).show()
    }
    
    fun isDownloaded(context: Context, fileName: String): Boolean {
        val file = java.io.File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "LivePlus/$fileName.mp4")
        return file.exists()
    }
}
