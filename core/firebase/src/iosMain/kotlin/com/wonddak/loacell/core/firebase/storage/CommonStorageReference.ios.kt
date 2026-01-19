package com.wonddak.loacell.core.firebase.storage

import cocoapods.FirebaseStorage.FIRStorageReference
import platform.Foundation.NSURL

actual typealias CommonStorageReference = FIRStorageReference

actual fun CommonStorageReference.getChildPath(path: String): CommonStorageReference {
    return this.child(path)
}

actual fun CommonStorageReference.downloadToFile(
    path: String,
    success: () -> Unit,
    fail: (Error) -> Unit
): CommonStorageReference {
    writeToFile(NSURL(string = path)) { _, error ->
        if (error == null) {
            success()
        } else {
            fail(Error(error.localizedDescription()))
        }
    }
    return this
}

actual fun CommonStorageReference.getDownloadUrl(success: (String) -> Unit) {
    this@getDownloadUrl.downloadURLWithCompletion { nsurl, _ ->
        success(nsurl.toString())
    }
}