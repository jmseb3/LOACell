package com.wonddak.loacell.storage

import cocoapods.FirebaseStorage.FIRStorage

actual typealias CommonFireStorage = FIRStorage

actual fun getFireStorage(): CommonFireStorage {
    return FIRStorage.storage()
}

actual fun CommonFireStorage.getCommonReference(): CommonStorageReference {
    return getFireStorage().reference()
}