package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRCollectionReference
import cocoapods.FirebaseFirestore.FIRDocumentReference
import cocoapods.FirebaseFirestore.FIRDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRFirestore
import platform.Foundation.NSError

actual class Error(error: NSError) {
    actual val errorMsg: String = error.localizedDescription

}

actual fun getFireStore(): CommonFireStore = CommonFireStore(FIRFirestore.firestore())

actual class CommonFireStore(
    private val ref: FIRFirestore
) {
    actual fun collection(path: String): CommonCollection =
        CommonCollection(ref.collectionWithPath(path))
}

actual class CommonCollection(
    private val ref: FIRCollectionReference
) {
    actual val id: String = ref.collectionID
    actual val parent: CommonDocument? = ref.parent?.let { CommonDocument(it) }

    actual fun document(): CommonDocument = CommonDocument(ref.documentWithAutoID())
    actual fun document(documentPath: String): CommonDocument =
        CommonDocument(ref.documentWithPath(documentPath))
}

actual class CommonDocument(
    private val ref: FIRDocumentReference
) {
    actual val id: String = ref.documentID
    actual val path: String = ref.path
    actual val parent: CommonCollection = CommonCollection(ref.parent)

    actual fun collection(collectionPath: String): CommonCollection =
        CommonCollection(ref.collectionWithPath(collectionPath))

    actual fun set(data: Map<String, Any>) {
        ref.setData(data as Map<Any?, *>)
    }

    actual fun set(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.setData(data as Map<Any?, *>) { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(Error(err))
            }
        }
    }

    actual fun update(data: Map<String, Any>) {
        ref.updateData(data as Map<Any?, *>)
    }

    actual fun update(field: String, value: Any) {
        ref.updateData(mapOf(field to value))
    }

    actual fun update(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.updateData(data as Map<Any?, *>) { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(Error(err))
            }
        }
    }

    actual fun update(
        field: String, value: Any, successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.updateData(mapOf(field to value)) { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(Error(err))
            }
        }
    }

    actual fun delete() {
        ref.deleteDocument()
    }

    actual fun delete(
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.deleteDocumentWithCompletion { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(Error(err))
            }
        }
    }


}

actual class CommonDocumentSnapshot(
    private val ref: FIRDocumentSnapshot
) {
    actual val exist: Boolean
        get() = ref.exists()
    actual val id: String
        get() = ref.documentID()
    actual val reference: CommonDocument
        get() = CommonDocument(ref.reference)
    actual val data: Map<String, Any>?
        get() = ref.data() as Map<String, Any>?

}
