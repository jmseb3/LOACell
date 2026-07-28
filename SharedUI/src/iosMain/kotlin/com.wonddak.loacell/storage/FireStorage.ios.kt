@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.wonddak.loacell.storage

import swiftPMImport.LoaCell.SharedUI.FIRStorage
import swiftPMImport.LoaCell.SharedUI.FIRStorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.Foundation.NSURL

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

actual fun CommonStorageReference.downloadToFile(
    path: String,
    successCompletion: () -> Unit,
    failCompletion: (error: FSError) -> Unit,
) {
    this.writeToFile(NSURL(string = path)) { url, error ->
        if (error == null) {
            successCompletion()
        } else {
            failCompletion(error)
        }
    }
}

actual fun CommonStorageReference.getDownloadUrl(success: (String) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        this@getDownloadUrl.downloadURLWithCompletion { nsurl, nsError ->
            success(nsurl.toString())
        }
    }
}
