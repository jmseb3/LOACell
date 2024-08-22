package com.wonddak.loacell.storage

import cocoapods.FirebaseStorage.FIRStorage
import cocoapods.FirebaseStorage.FIRStorageReference
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