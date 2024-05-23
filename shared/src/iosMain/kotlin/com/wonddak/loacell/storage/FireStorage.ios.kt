package com.wonddak.loacell.storage

import cocoapods.FirebaseStorage.FIRStorage
import cocoapods.FirebaseStorage.FIRStorageReference
import platform.Foundation.NSData
import platform.Foundation.NSError

actual typealias CommonFireStorage = FIRStorage
actual typealias CommonStorageReference = FIRStorageReference
actual typealias ByteData = NSData
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

actual fun CommonStorageReference.downloadByByte(
    successCompletion: (data: ByteData?) -> Unit,
    failCompletion: (error: FSError) -> Unit
) {
    this.dataWithMaxSize(maxSize = ONE_MEGABYTE) { data, error ->
        if (error == null) {
            successCompletion(data)
        } else {
            failCompletion(error)
        }
    }
}