package com.quickhandslogistics.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.net.URLConnection

object ImageUtils {
    @Throws(IOException::class)
    fun saveSignatureTemporary(bitmap: Bitmap, quality: Int, context: Context): File {
        val imageFileName = "TEMP_" + System.currentTimeMillis() + ".jpg"
        val fileDirectory = context.getExternalFilesDir(null)
        val destinationFile = File(fileDirectory, imageFileName)

        val outputStream = FileOutputStream(destinationFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)

        outputStream.close()
        bitmap.recycle()

        return destinationFile
    }
}

internal class FetchMimeType(private val url: String, private val listener: OnFetchCompleteListener) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String {
        var mimeType = ""
        try {
            val connection = URL(url).openConnection()
            mimeType = connection.contentType
        } catch (ignored: Exception) {
        }
        if (mimeType.isEmpty()) {
            try {
                mimeType = URLConnection.guessContentTypeFromName(url)
            } catch (ignored: Exception) {
            }
        }
        return mimeType
    }

    override fun onPostExecute(mimeType: String) {
        super.onPostExecute(mimeType)
        listener.onFetchMimeType(mimeType)
    }
}

internal interface OnFetchCompleteListener {
    fun onFetchMimeType(mimeType: String)
}