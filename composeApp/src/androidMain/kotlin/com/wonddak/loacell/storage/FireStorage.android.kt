package com.wonddak.loacell.storage

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.File

actual typealias CommonFireStorage = FirebaseStorage
actual typealias CommonStorageReference = StorageReference
actual typealias FSError = java.lang.Exception

actual fun getFireStorage(): CommonFireStorage {
    return Firebase.storage
}

actual fun CommonFireStorage.getCommonReference(): CommonStorageReference {
    return this.reference
}

actual fun CommonStorageReference.getChildPath(path: String): CommonStorageReference {
    return this.child(path)
}

actual fun CommonStorageReference.downloadToFile(
    path: String,
    successCompletion: () -> Unit,
    failCompletion: (error: FSError) -> Unit,
) {
    this.getFile(File(path))
        .addOnSuccessListener {
            successCompletion()
        }
        .addOnFailureListener {
            failCompletion(it)
        }
}