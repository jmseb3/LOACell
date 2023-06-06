package com.wonddak.loacell.store

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

actual class Error(error :Exception?) {
    actual val errorMsg :String = error?.localizedMessage ?: "unknown error"
}

actual fun getFireStore(): CommonFireStore = CommonFireStore(Firebase.firestore)

actual class CommonFireStore(
    private val ref: FirebaseFirestore
) {
    actual fun collection(path: String): CommonCollection = CommonCollection(ref.collection(path))
}

actual class CommonCollection(
    private val ref: CollectionReference
) {
    actual val id: String = ref.id
    actual val parent: CommonDocument? = ref.parent?.let { CommonDocument(it) }

    actual fun document(): CommonDocument = CommonDocument(ref.document())

    actual fun document(documentPath: String): CommonDocument =
        CommonDocument(ref.document(documentPath))
}

actual class CommonDocument(
    private val ref: DocumentReference
) {
    actual val id: String = ref.id
    actual val path: String = ref.path
    actual val parent: CommonCollection = CommonCollection(ref.parent)

    actual fun collection(collectionPath: String): CommonCollection =
        CommonCollection(ref.collection(collectionPath))

    actual fun set(data: Map<String, Any>) {
        ref.set(data)
    }

    actual fun set(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error :Error) -> Unit
    ) {
        ref.set(data)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun update(data: Map<String, Any>) {
        ref.update(data)
    }

    actual fun update(field : String , value :Any) {
        ref.update(field,value)
    }

    actual fun update(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error :Error) -> Unit
    ) {
        ref.update(data)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun update(
        field : String ,
        value :Any,
        successAction: () -> Unit,
        failAction: (error :Error) -> Unit
    ) {
        ref.update(field,value)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun delete() {
        ref.delete()
    }

    actual fun delete(
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.delete()
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }


}

actual class CommonDocumentSnapshot(
    private val ref: DocumentSnapshot
) {
    actual val exist: Boolean
        get() = ref.exists()
    actual val id: String
        get() = ref.id
    actual val reference: CommonDocument
        get() = CommonDocument(ref.reference)

    actual val data = ref.data as Map<String, Any>?

}