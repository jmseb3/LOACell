package com.wonddak.loacell.util

internal const val ASSETS_PATH = "assets"

expect class FileUtil {

    fun getCachePath(path: String): String
    fun getAssetPath(): String
    fun isExist(path: String): Boolean
    fun readFile(path: String): String
}

class FileHelper(
    private val util: FileUtil,
) {
    fun getAssetFilePath(fileName: String): String = (util.getAssetPath() + fileName)

    fun isExistAsset(fileName: String) = util.isExist(getAssetFilePath(fileName))

    fun readFile(path: String): String = util.readFile(path)

    fun getCacheImage(): String = util.getCachePath("image_cache")
}