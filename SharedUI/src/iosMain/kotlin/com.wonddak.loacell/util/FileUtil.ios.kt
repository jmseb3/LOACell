@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.wonddak.loacell.util

import io.github.aakira.napier.Napier
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringWithContentsOfFile
import dev.zacsweers.metro.Inject

@Inject
actual class FileUtil {

    private val fileManager = NSFileManager.defaultManager

    private fun providePath(path: String): String {
        val directory = fileManager.URLsForDirectory(
            directory = NSCachesDirectory,
            inDomains = NSUserDomainMask,
        ).filterIsInstance<NSURL>().first()

        val filePath = directory.URLByAppendingPathComponent(path)
        fileManager.createDirectoryAtPath(
            path = filePath.toString(),
            withIntermediateDirectories = true,
            null,
            null
        )
        return filePath.toString()
    }

    actual fun getCachePath(path: String): String = providePath(path)

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
        return fileManager.fileExistsAtPath(path = path.replace("file://", ""))
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

    actual fun delete(path: String) : Boolean {
        return fileManager.removeItemAtPath(path = path.replace("file://", ""),error = null)
    }
}
