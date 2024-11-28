package com.wonddak.loacell.ui.raidRoom.raid

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.wonddak.loacell.AppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual fun shareImage(bitmap: ImageBitmap?) {
    bitmap?.let { share(bitmap) }
}

suspend fun Bitmap.getUri(context: Context): Uri = withContext(Dispatchers.IO) {
    val sharedImagesDir = File(context.filesDir, "share_images")

    if (!sharedImagesDir.exists()) {
        sharedImagesDir.mkdirs()
    }

    val tempFileToShare: File = sharedImagesDir.resolve("share_picture.png")
    val outputStream = FileOutputStream(tempFileToShare)
    compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    outputStream.flush()

    FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        tempFileToShare
    )
}

fun share(imageBitmap: ImageBitmap) {
    CoroutineScope(Dispatchers.Main).launch {
        val context = AppContext.get()
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val mimeType = "image/png"
        val mimeTypeArray = arrayOf(mimeType)
        intentShareFile.setType(mimeType)
        val uri = imageBitmap.asAndroidBitmap().getUri(context)
        intentShareFile.clipData = ClipData(
            "공격대 이미지 입니다.",
            mimeTypeArray,
            ClipData.Item(uri)
        )
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
        intentShareFile.putExtra(Intent.EXTRA_TITLE, "공격대 이미지 입니다.")
        intentShareFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intentShareFile, null))
    }
}
