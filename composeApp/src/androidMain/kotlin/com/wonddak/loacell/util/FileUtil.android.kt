package com.wonddak.loacell.util

import android.content.Context
import java.io.File

actual class FileUtil(
    private val context: Context,
) {
    actual fun getCachePath(): String {
        return context.cacheDir.path
    }

    actual fun getAssetPath(): String {
        val file = File(context.cacheDir, ASSETS_PATH)
        file.mkdirs()
        return file.path
    }

    actual fun isExist(path: String): Boolean {
        return File(path).exists()
    }

}