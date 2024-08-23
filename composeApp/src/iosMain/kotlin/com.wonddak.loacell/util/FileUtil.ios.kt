package com.wonddak.loacell.util

import io.github.aakira.napier.Napier
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile

actual class FileUtil {

    private val fileManager = NSFileManager.defaultManager

    private fun providePath(path: String): String {
        val dirPaths = fileManager.URLsForDirectory(
            directory = NSCachesDirectory,
            inDomains = NSUserDomainMask
        ) as List<NSURL>
        val filePath = dirPaths[0].URLByAppendingPathComponent(path)
        return filePath.toString()
    }

    actual fun getCachePath(): String = providePath("")

    actual fun getAssetPath(): String {
        val assetPath = providePath(ASSETS_PATH)
        fileManager.createDirectoryAtPath(
            path = assetPath,
            withIntermediateDirectories = true,
            null,
            null
        )
        return assetPath
    }

    actual fun isExist(path: String): Boolean {
        return fileManager.fileExistsAtPath(path = path)
    }

    actual fun readFile(path: String): String {
        return runCatching {
            NSString.stringWithContentsOfFile(
                path.replace("file://", ""),
                encoding = NSUTF8StringEncoding,
                null
            ) as String
        }.onFailure {
            Napier.e(tag = "readFile", throwable = it) { "error to read file from $path" }
        }.getOrDefault("{}")
    }
}