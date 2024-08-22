package com.wonddak.loacell.util

import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent

actual class FileUtil {

    private val fileManager = NSFileManager.defaultManager

    private fun providePath(path: String): String {
        val dirPaths = fileManager.URLsForDirectory(
            directory = NSCachesDirectory,
            inDomains = NSUserDomainMask
        ) as List<NSURL>
        val filePath = dirPaths[0].URLByAppendingPathComponent(path)
        return filePath!!.path!!
    }

    actual fun getCachePath(): String = providePath("")

    actual fun getAssetPath(): String {
        return providePath(ASSETS_PATH)
    }

    actual fun isExist(path: String): Boolean {
        return fileManager.fileExistsAtPath(path = path)
    }

}