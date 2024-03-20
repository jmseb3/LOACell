package com.wonddak.loacell.android.util

import android.content.ClipData
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    private const val FILE_PROVIDER = "com.wonddak.loacell.android.fileprovider"

    fun requestShare(
        context: Context,
        roomId: String,
        raidId: String,
        bitmapImage: Bitmap
    ) {
        saveRaidImageToCache(context, roomId, raidId, bitmapImage).also {file ->
            if (file != null) {
                shareRaidImage(context,file)
            }
        }
    }

    private fun saveRaidImageToCache(
        context: Context,
        roomId: String,
        raidId: String,
        bitmapImage: Bitmap
    ): File? {
        val cw = ContextWrapper(context)
        val directory: File = File(cw.cacheDir, "/raid/$roomId")

        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Create imageDir
        val myPath = File(directory, "$raidId.jpg")

        runCatching {
            FileOutputStream(myPath).use { fos ->
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        }.onFailure {
            it.printStackTrace()
            return null
        }

        return myPath
    }

    private fun shareRaidImage(
        context: Context,
        file: File
    ) {
        Log.d("JWH", "path : ${file.absolutePath}")
        val uri = FileProvider.getUriForFile(context, FILE_PROVIDER, file)
        Log.d("JWH", "uri : $uri")

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/jpg"
            clipData = ClipData.newRawUri(null, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, null));
    }
}