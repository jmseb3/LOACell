package com.wonddak.loacell.core.firebase.storage

import com.google.firebase.storage.StorageReference
import java.io.File

actual typealias CommonStorageReference = StorageReference

actual fun CommonStorageReference.getChildPath(path: String): CommonStorageReference {
    return this.child(path)
}

actual fun CommonStorageReference.downloadToFile(
    path: String,
    success: () -> Unit,
    fail: (Error) -> Unit
): CommonStorageReference {
    this.getFile(File(path))
        .addOnSuccessListener {
            success()
        }
        .addOnFailureListener {
            fail(Error(it))
        }
    return this
}

actual fun CommonStorageReference.getDownloadUrl(success: (String) -> Unit) {
    this.downloadUrl
        .addOnSuccessListener {
            success(it.toString())
        }
}