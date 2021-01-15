package com.quickhandslogistics.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap

object DownloadUtils {

    fun downloadFile(fileUrl: String, mimeType: String, context: Context): Long {
        var fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1)
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        fileName = if (fileExtension.isNullOrEmpty()) {
            "QHL-$fileName"
        } else {
            "QHL-$fileName.$fileExtension"
        }

        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setTitle(fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.enqueue(request)
    }
}