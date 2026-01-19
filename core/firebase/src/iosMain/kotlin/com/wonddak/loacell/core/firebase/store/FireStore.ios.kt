package com.wonddak.loacell.core.firebase.store

import cocoapods.FirebaseFirestoreInternal.FIRCollectionReference
import cocoapods.FirebaseFirestoreInternal.FIRDocumentReference
import cocoapods.FirebaseFirestoreInternal.FIRDocumentSnapshot
import cocoapods.FirebaseFirestoreInternal.FIRFieldPath
import cocoapods.FirebaseFirestoreInternal.FIRFieldValue
import cocoapods.FirebaseFirestoreInternal.FIRFilter
import cocoapods.FirebaseFirestoreInternal.FIRFirestore
import cocoapods.FirebaseFirestoreInternal.FIRListenerRegistrationProtocol
import cocoapods.FirebaseFirestoreInternal.FIRQuery
import cocoapods.FirebaseFirestoreInternal.FIRQuerySnapshot
import cocoapods.FirebaseFirestoreInternal.FIRWriteBatch
import kotlinx.cinterop.ExperimentalForeignApi


actual fun getFireStore(): CommonFireStore = CommonFireStore(FIRFirestore.firestore())

actual class CommonFireStore(
    private val ref: FIRFirestore,
) {
    actual fun collection(path: String): CommonCollection =
        CommonCollection(ref.collectionWithPath(path))

    @OptIn(ExperimentalForeignApi::class)
    actual fun runTransaction(
        refDoc: CommonDocument,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
        action: (transaction: CommonDocumentSnapshot) -> Unit,
    ) {
        ref.runTransactionWithBlock(
            updateBlock = { transaction, errorPointer ->
                if (transaction != null) {
                    val snapshot = transaction.getDocument(refDoc.ref, errorPointer)
                    if (snapshot != null) {
                        action(CommonDocumentSnapshot(snapshot))
                    }
                }
            },
            completion = { _, error ->
                if (error == null) {
                    successAction()
                } else {
                    failAction(Error(error))
                }
            }
        )
    }

    actual fun runBatch(write: (batch: CommonBatch) -> Unit) {
        val batch = ref.batch()
        write(CommonBatch(batch))
        batch.commit()
    }

    actual fun runBatch(
        write: (batch: CommonBatch) -> Unit,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        val batch = ref.batch()
        write(CommonBatch(batch))
        batch.commitWithCompletion { error ->
            if (error != null) {
                successAction()
            } else {
                failAction(Error(error))
            }
        }
    }
}

actual class CommonBatch(
    val ref: FIRWriteBatch,
) {
    actual fun update(
        doc: CommonDocument,
        field: String,
        value: Any,
    ) {
        ref.updateData(mapOf(field to value), doc.ref)
    }

    actual fun update(
        doc: CommonDocument,
        field: String,
        value: CommonFieldValue,
    ) {
        ref.updateData(mapOf(field to value.ref), doc.ref)
    }
}

actual class CommonCollection(
    private val ref: FIRCollectionReference,
) {
    actual val id: String = ref.collectionID
    actual val parent: CommonDocument? = ref.parent?.let { CommonDocument(it) }

    actual fun document(): CommonDocument = CommonDocument(ref.documentWithAutoID())
    actual fun document(documentPath: String): CommonDocument =
        CommonDocument(ref.documentWithPath(documentPath))

    actual fun getListenerRegistration(
        successAction: (a: CommonQuerySnapshot) -> Unit,
        failAction: (error: Error?) -> Unit,
    ): CommonListenerRegistration {
        return CommonListenerRegistration(
            ref.addSnapshotListener { value, error ->
                if (error != null) {
                    failAction(Error(error))
                }
                if (value != null) {
                    successAction(CommonQuerySnapshot(value))
                } else {
                    failAction(null)
                }
            }
        )
    }

    actual fun where(filter: CommonFilter): CommonQuery {
        return CommonQuery(ref.queryWhereFilter(filter.ref))
    }

    actual fun whereIn(
        filed: String,
        list: List<Any>,
    ): CommonQuery {
        return CommonQuery(ref.queryWhereField(field = filed, `in` = list))
    }

    actual fun whereIn(
        filed: CommonFieldPath,
        list: List<Any>,
    ): CommonQuery {
        return CommonQuery(ref.queryWhereFieldPath(path = filed.ref, `in` = list))
    }
}

actual class CommonFilter(
    val ref: FIRFilter,
) {
    actual companion object {
        actual fun equalTo(
            filed: String,
            value: Any,
        ): CommonFilter = CommonFilter(FIRFilter.filterWhereField(filed, isEqualTo = value))

        actual fun arrayContains(
            filed: String,
            value: Any,
        ): CommonFilter = CommonFilter(FIRFilter.filterWhereField(filed, arrayContains = value))


        actual fun or(vararg filters: CommonFilter): CommonFilter {
            val convertFilter = filters.map { it.ref }
            return CommonFilter(FIRFilter.orFilterWithFilters(convertFilter))
        }

    }

}

actual class CommonDocument(
    val ref: FIRDocumentReference,
) {
    actual val id: String = ref.documentID
    actual val path: String = ref.path
    actual val parent: CommonCollection = CommonCollection(ref.parent)

    actual fun collection(collectionPath: String): CommonCollection =
        CommonCollection(ref.collectionWithPath(collectionPath))

    actual fun getListenerRegistration(
        successAction: (a: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error?) -> Unit,
    ): CommonListenerRegistration {
        return CommonListenerRegistration(
            ref.addSnapshotListener { value, error ->
                if (error != null) {
                    failAction(Error(error))
                    return@addSnapshotListener
                }
                if (value != null) {
                    successAction(CommonDocumentSnapshot(value))
                } else {
                    failAction(null)
                }

            }
        )
    }

    actual fun set(data: Map<String, Any>) {
        ref.setData(data as Map<Any?, *>)
    }

    actual fun set(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        runCatching {
            set(data)
        }.onSuccess {
            successAction()
        }.onFailure {
            failAction(Error(null))
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
        failAction: (error: Error) -> Unit,
    ) {
        runCatching {
            update(data)
        }.onSuccess {
            successAction()
        }.onFailure {
            failAction(Error(null))
        }
    }

    actual fun update(
        field: String, value: Any, successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        runCatching {
            update(mapOf(field to value))
        }.onSuccess {
            successAction()
        }.onFailure {
            failAction(Error(null))
        }
    }

    actual fun update(
        field: String, value: CommonFieldValue, successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        runCatching {
            println("ROOM Store 1")
            update(mapOf(field to value.ref))
        }.onSuccess {
            println("ROOM Store 2")
            successAction()
        }.onFailure {
            println("ROOM Store 3")
            failAction(Error(null))
        }
    }

    actual fun delete() {
        ref.deleteDocument()
    }


    actual fun delete(
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        runCatching {
            delete()
        }.onSuccess {
            successAction()
        }.onFailure {
            failAction(Error(it))
        }
    }

    actual fun get(
        successAction: (documentSnapshot: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        ref.getDocumentWithCompletion { firDocumentSnapshot, nsError ->
            if (nsError == null) {
                if (firDocumentSnapshot != null) {
                    successAction(CommonDocumentSnapshot(firDocumentSnapshot))
                } else {
                    failAction(Error(message = null))
                }
            } else {
                failAction(Error(nsError.localizedDescription))
            }
        }
    }


}

actual class CommonDocumentSnapshot(
    private val ref: FIRDocumentSnapshot,
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

actual class CommonQuerySnapshot(
    val ref: FIRQuerySnapshot,
) {
    actual val documents: List<CommonDocumentSnapshot> =
        (ref.documents.filterIsInstance<FIRDocumentSnapshot>()).map { CommonDocumentSnapshot(it) }
}

actual class CommonQuery(
    val ref: FIRQuery,
) {

    actual fun get(
        successAction: (querySnapshot: CommonQuerySnapshot) -> Unit,
        failAction: (error: Error) -> Unit,
    ) {
        ref.getDocumentsWithCompletion { firQuerySnapshot, nsError ->
            if (nsError == null) {
                if (firQuerySnapshot != null) {
                    successAction(CommonQuerySnapshot(firQuerySnapshot))
                } else {
                    failAction(Error(message = null))
                }
            } else {
                failAction(Error(nsError.localizedDescription()))
            }
        }
    }

    actual fun getListenerRegistration(
        successAction: (a: List<CommonDocumentSnapshot>) -> Unit,
        failAction: (error: Error?) -> Unit,
    ): CommonListenerRegistration {
        return CommonListenerRegistration(
            ref.addSnapshotListener { value, error ->
                if (error != null) {
                    failAction(Error(error.localizedDescription()))
                    return@addSnapshotListener
                }
                if (value != null) {
                    successAction(value.documents.map { CommonDocumentSnapshot(it as FIRDocumentSnapshot) })
                } else {
                    failAction(null)
                }

            }
        )
    }

}

actual class CommonFieldValue(
    val ref: FIRFieldValue,
) {
    actual companion object {
        actual fun arrayUnion(value: Any): CommonFieldValue {
            return CommonFieldValue(FIRFieldValue.fieldValueForArrayUnion(listOf(value)))
        }

        actual fun arrayRemove(value: Any): CommonFieldValue {
            return CommonFieldValue(FIRFieldValue.fieldValueForArrayRemove(listOf(value)))
        }
    }
}

actual class CommonFieldPath(
    val ref: FIRFieldPath,
) {
    actual companion object {
        actual fun documentId(): CommonFieldPath = CommonFieldPath(FIRFieldPath.documentID())
    }
}

actual class CommonListenerRegistration(
    val ref: FIRListenerRegistrationProtocol,
) {
    actual fun remove() = ref.remove()
}