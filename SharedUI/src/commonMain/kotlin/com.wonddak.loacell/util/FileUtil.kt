package com.wonddak.loacell.util

import io.github.aakira.napier.Napier

internal const val ASSETS_PATH = "assets"

expect class FileUtil {

    fun getCachePath(path: String): String
    fun getAssetPath(): String
    fun isExist(path: String): Boolean
    fun readFile(path: String): String
    fun delete(path: String) : Boolean
}

class FileHelper(
    private val util: FileUtil,
) {
    fun getAssetFilePath(fileName: String): String = (util.getAssetPath() + fileName).also {
        Napier.d(tag= "FILE") { "file : $fileName path : $it" }

    }

    fun isExistAsset(fileName: String) = util.isExist(getAssetFilePath(fileName)).also {
        Napier.d(tag= "FILE") { "file : $fileName is exist : $it" }
    }

    fun deleteAssetFile(fileName: String) = util.delete(getAssetFilePath(fileName)).also {
        Napier.d(tag= "FILE") { "file : $fileName is delete : $it" }
    }

    fun readFile(path: String): String = util.readFile(path)

    fun getCacheImage(): String = util.getCachePath("image_cache")
}