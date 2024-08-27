package com.wonddak.loacell

import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual fun getCacheDir(): String {
    val fileManager = NSFileManager.defaultManager

    val dirPaths = fileManager.URLsForDirectory(
        directory = NSCachesDirectory,
        inDomains = NSUserDomainMask
    ) as List<NSURL>
    val filePath = dirPaths[0].URLByAppendingPathComponent("image_cache")
    return filePath.toString()
}