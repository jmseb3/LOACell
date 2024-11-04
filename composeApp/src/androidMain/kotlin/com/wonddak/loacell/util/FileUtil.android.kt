package com.wonddak.loacell.util

import android.content.Context
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

actual class FileUtil(
    private val context: Context,
) {
    actual fun getCachePath(path: String): String {
        return context.cacheDir.path + "/" + path
    }

    actual fun getAssetPath(): String {
        val file = File(context.cacheDir, ASSETS_PATH)
        file.mkdirs()
        return file.path + "/"
    }

    actual fun isExist(path: String): Boolean {
        return File(path).exists()
    }

    actual fun readFile(path: String): String {
        val file = File(path)
        val data = StringBuilder()
        try {
            file.inputStream().use { fis ->
                InputStreamReader(fis).use { isr ->
                    BufferedReader(isr).use { bufferedReader ->
                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            data.append(line)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data.toString()
    }

    actual fun delete(path: String) : Boolean {
        val file = File(path)
        return file.delete()
    }

}