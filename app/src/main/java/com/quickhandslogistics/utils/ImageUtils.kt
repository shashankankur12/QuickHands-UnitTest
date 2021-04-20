package com.quickhandslogistics.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
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

    fun resizeAndCompressImageBeforeSend(context: Context, filePath: String?, filename: String): String? {
        val compressQuality = 70
        val imageResolution = 800
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(
            options,
            imageResolution,
            imageResolution
        )
        options.inJustDecodeBounds = false
        var bmpPic = BitmapFactory.decodeFile(filePath, options)
        val bos = ByteArrayOutputStream()
        bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bos)
        if (bmpPic.height >= imageResolution && bmpPic.width >= imageResolution) {
            bmpPic = getResizedBitmap(bmpPic, imageResolution)
        }
        try {
            val bmpFile = FileOutputStream(context.cacheDir.toString() + filename)
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile)
            bmpFile.flush()
            bmpFile.close()
        } catch (e: java.lang.Exception) {
        }
        return context.cacheDir.toString() + filename
    }
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                && halfWidth / inSampleSize >= reqWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun getImagePath(activity: Activity, uri: Uri?): String? {
        var path: String? = null
        try {
            if (uri != null) {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = activity.contentResolver.query(uri, projection, null, null, null)
                    ?: return null
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                path = cursor.getString(column_index)
                cursor.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return path
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