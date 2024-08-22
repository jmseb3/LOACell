package com.wonddak.loacell.util

import io.github.aakira.napier.Napier

internal const val ASSETS_PATH = "assets"

expect class FileUtil {

    fun getCachePath(): String
    fun getAssetPath(): String
    fun isExist(path: String): Boolean
    fun readFile(path: String): String
}

class FileHelper(
    private val util: FileUtil,
) {
    fun getAssetFilePath(fileName: String): String = (util.getAssetPath() + "/" + fileName).also {
        Napier.d(tag = "FILE") { it }
    }

    fun isExistAsset(fileName: String) = util.isExist(getAssetFilePath(fileName))

    fun readFile(path: String): String = util.readFile(path)
}