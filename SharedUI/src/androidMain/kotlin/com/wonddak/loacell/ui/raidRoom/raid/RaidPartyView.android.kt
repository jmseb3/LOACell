package com.wonddak.loacell.ui.raidRoom.raid

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.wonddak.loacell.AppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

actual suspend fun shareImage(bitmap: ImageBitmap?) {
    bitmap?.let { share(bitmap) }
}

internal suspend fun Bitmap.getUri(context: Context): Uri = withContext(Dispatchers.IO) {
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

internal suspend fun share(imageBitmap: ImageBitmap) {
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

actual fun saveImageBitmap(bitmap: ImageBitmap?, complete: () -> Unit) {
    if (bitmap == null) {
        return
    }
    val context = AppContext.get()

    // Android Bitmap으로 변환
    val androidBitmap = bitmap.asAndroidBitmap()

    // DCIM 경로에 저장
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
    }

    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        val outputStream: OutputStream? = contentResolver.openOutputStream(it)
        outputStream?.use { stream ->
            androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        complete()
    }
}
