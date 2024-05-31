package com.wonddak.loacell.storage

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import org.koin.java.KoinJavaComponent
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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

fun CommonStorageReference.downloadToFile(
    filePath: String,
    successCompletion: () -> Unit = {},
    failCompletion: (error: FSError) -> Unit = {}
) {
    this.getFile(File(filePath))
        .addOnSuccessListener {
            successCompletion()
        }
        .addOnFailureListener {
            failCompletion(it)
        }
}

actual class SynergyReferenceHelper {
    private val context: Context = KoinJavaComponent.getKoin().get()

    private val file = File(context.filesDir, FileName)
    actual fun isExist(): Boolean {
        return file.exists()
    }

    actual fun downloadFile(callBack: (Map<String, String>) -> Unit) {
        if (!isExist()) {
            FireStorageReferenceHelper
                .getSynergyReference()
                .downloadToFile(
                    file.path,
                    successCompletion = {
                        callBack(readFile())
                    }
                )
        } else {
            callBack(readFile())
        }
    }

    private fun readFile(): Map<String, String> {
        val data = StringBuilder()
        try {
            file.inputStream().use { fis ->
                InputStreamReader(fis).use { isr ->
                    BufferedReader(isr).use { bufferedReader ->
                        var line: String?
                        while (bufferedReader.readLine().also { line = it } != null) {
                            data.append(line)
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
        return FireStorageReferenceHelper.jsonStringToData(data.toString())
    }

}