package com.wonddak.loacell.storage

import com.google.firebase.storage.FirebaseStorage

actual typealias CommonFireStorage = FirebaseStorage

actual fun getFireStorage(): CommonFireStorage {
    return FirebaseStorage.getInstance()
}

actual fun CommonFireStorage.getCommonReference(): CommonStorageReference {
    return this.reference
}