package com.wonddak.loacell.storage

import cocoapods.FirebaseStorage.FIRStorage
import cocoapods.FirebaseStorage.FIRStorageReference
import io.github.aakira.napier.Napier
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.URLByAppendingPathComponent
import platform.Foundation.stringWithContentsOfFile

actual typealias CommonFireStorage = FIRStorage
actual typealias CommonStorageReference = FIRStorageReference
actual typealias FSError = NSError

actual fun getFireStorage(): CommonFireStorage {
    return FIRStorage.storage()
}

actual fun CommonFireStorage.getCommonReference(): CommonStorageReference {
    return getFireStorage().reference()
}

actual fun CommonStorageReference.getChildPath(path: String): CommonStorageReference {
    return this.child(path)
}

fun CommonStorageReference.downloadToFile(
    filePath: NSURL,
    successCompletion: () -> Unit = {},
    failCompletion: (error: FSError) -> Unit = {}
) {
    this.writeToFile(filePath) { url, error ->
        if (error == null) {
            successCompletion()
        } else {
            failCompletion(error)
        }
    }
}

actual class SynergyReferenceHelper {

    private fun providePath(): String {
        val dirPaths = NSFileManager.defaultManager.URLsForDirectory(
            directory = NSCachesDirectory,
            inDomains = NSUserDomainMask
        ) as List<NSURL>
        val filePath = dirPaths[0].URLByAppendingPathComponent(FileName)
        Napier.d(tag = "SynergyReferenceHelper") { "${filePath?.filePathURL()}" }
        return filePath!!.path!!
    }

    private val filePath = providePath()
    private val fileUrl: NSURL = NSURL(string = filePath)
    actual fun isExist(): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path = filePath).also {
            Napier.d(tag = "SynergyReferenceHelper") { "$filePath exists : $it" }
        }
    }

    actual fun downloadFile(callBack: (Map<String, String>) -> Unit) {
        if (!isExist()) {
            Napier.d(tag = "SynergyReferenceHelper") { "file no exist start download.." }
            FireStorageReferenceHelper
                .getSynergyReference()
                .downloadToFile(
                    fileUrl,
                    successCompletion = {
                        callBack(readFile())
                    }
                )
        } else {
            Napier.d(tag = "SynergyReferenceHelper") { "file exist pass download.." }
            callBack(readFile())
        }
    }

    private fun readFile(): Map<String, String> {
        val jsonString = runCatching {
            NSString.stringWithContentsOfFile(
                path = filePath,
                encoding = NSUTF8StringEncoding,
                null
            ) as String
        }.getOrDefault("{}")
        return FireStorageReferenceHelper.jsonStringToData(jsonString)
    }
}