package com.wonddak.loacell.storage

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

actual typealias CommonFireStorage = FirebaseStorage
actual typealias CommonStorageReference = StorageReference
actual typealias ByteData = ByteArray
actual typealias FSError = java.lang.Exception

actual fun getFireStorage(): CommonFireStorage {
    return Firebase.storage
}
actual fun CommonFireStorage.getCommonReference(): CommonStorageReference {
    return this.reference
}
actual fun CommonStorageReference.getChildPath(path:String): CommonStorageReference {
    return this.child(path)
}

actual fun CommonStorageReference.downloadByByte(
    successCompletion: (data:ByteData?) -> Unit,
    failCompletion :(error : FSError) -> Unit
) {
    this.getBytes(ONE_MEGABYTE)
        .addOnSuccessListener {
            successCompletion(it)
        }
        .addOnFailureListener {
            failCompletion(it)
        }
}