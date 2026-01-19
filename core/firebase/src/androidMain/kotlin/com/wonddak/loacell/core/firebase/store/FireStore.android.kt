package com.wonddak.loacell.core.firebase.store

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


actual fun getFireStore(): CommonFireStore = CommonFireStore(FirebaseFirestore.getInstance())

actual class CommonFireStore(
    private val ref: FirebaseFirestore
) {
    actual fun collection(path: String): CommonCollection = CommonCollection(ref.collection(path))

    actual fun runTransaction(
        refDoc: CommonDocument,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
        action: (transaction: CommonDocumentSnapshot) -> Unit
    ) {
        ref.runTransaction { transaction ->
            val snapshot = transaction.get(refDoc.ref)
            action(CommonDocumentSnapshot(snapshot))
        }
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun runBatch(
        write: (batch: CommonBatch) -> Unit
    ) {
        val batch = ref.batch()
        write(CommonBatch(batch))
        batch.commit()
    }

    actual fun runBatch(
        write: (batch: CommonBatch) -> Unit,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        val batch = ref.batch()
        write(CommonBatch(batch))
        batch.commit().addOnSuccessListener {
            successAction()
        }.addOnFailureListener {
            failAction(Error(it))
        }
    }
}

actual class CommonBatch(
    val ref: WriteBatch
) {
    actual fun update(
        doc: CommonDocument,
        field: String,
        value: Any
    ) {
        ref.update(doc.ref, field, value)
    }

    actual fun update(
        doc: CommonDocument,
        field: String,
        value: CommonFieldValue
    ) {
        ref.update(doc.ref, field, value.ref)
    }
}

actual class CommonCollection(
    private val ref: CollectionReference
) {
    actual val id: String = ref.id
    actual val parent: CommonDocument? = ref.parent?.let { CommonDocument(it) }

    actual fun document(): CommonDocument = CommonDocument(ref.document())

    actual fun document(documentPath: String): CommonDocument =
        CommonDocument(ref.document(documentPath))

    actual fun getListenerRegistration(
        successAction: (a: CommonQuerySnapshot) -> Unit,
        failAction: (error: Error?) -> Unit
    ): CommonListenerRegistration {
        return CommonListenerRegistration(
            ref.addSnapshotListener { value, error ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (error != null) {
                        failAction(Error(error))
                    }
                    if (value != null) {
                        successAction(CommonQuerySnapshot(value))
                    } else {
                        failAction(null)
                    }
                }
            }
        )
    }

    actual fun where(filter: CommonFilter): CommonQuery {
        return CommonQuery(ref.where(filter.ref))
    }

    actual fun whereIn(filed: String, list: List<Any>): CommonQuery {
        return CommonQuery(ref.whereIn(filed, list))
    }

    actual fun whereIn(
        filed: CommonFieldPath,
        list: List<Any>
    ): CommonQuery {
        return CommonQuery(ref.whereIn(filed.ref, list))
    }
}

actual class CommonFilter(
    val ref: Filter
) {
    actual companion object {
        actual fun equalTo(
            filed: String,
            value: Any
        ): CommonFilter = CommonFilter(Filter.equalTo(filed, value))

        actual fun arrayContains(
            filed: String,
            value: Any
        ): CommonFilter = CommonFilter(Filter.arrayContains(filed, value))


        actual fun or(vararg filters: CommonFilter): CommonFilter {
            val convertFilter = filters.map { it.ref }.toTypedArray()
            return CommonFilter(Filter.or(*convertFilter))
        }

    }

}

actual class CommonDocument(
    val ref: DocumentReference
) {
    actual val id: String = ref.id
    actual val path: String = ref.path
    actual val parent: CommonCollection = CommonCollection(ref.parent)

    actual fun collection(collectionPath: String): CommonCollection =
        CommonCollection(ref.collection(collectionPath))

    actual fun getListenerRegistration(
        successAction: (a: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error?) -> Unit
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
        ref.set(data)
    }

    actual fun set(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
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

    actual fun update(field: String, value: Any) {
        ref.update(field, value)
    }

    actual fun update(
        data: Map<String, Any>,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
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
        field: String,
        value: Any,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.update(field, value)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun update(
        field: String,
        value: CommonFieldValue,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.update(field, value.ref)
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

    actual fun get(
        successAction: (documentSnapshot: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.get()
            .addOnSuccessListener {
                successAction(CommonDocumentSnapshot(it))
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

actual class CommonQuerySnapshot(
    val ref: QuerySnapshot
) {
    actual val documents: List<CommonDocumentSnapshot> =
        ref.documents.map { CommonDocumentSnapshot(it) }
}

actual class CommonQuery(
    val ref: Query
) {

    actual fun get(
        successAction: (querySnapshot: CommonQuerySnapshot) -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.get()
            .addOnSuccessListener {
                successAction(CommonQuerySnapshot(it))
            }
            .addOnFailureListener {
                failAction(Error(it))
            }
    }

    actual fun getListenerRegistration(
        successAction: (a: List<CommonDocumentSnapshot>) -> Unit,
        failAction: (error: Error?) -> Unit,
    ): CommonListenerRegistration {
        return CommonListenerRegistration(
            ref.addSnapshotListener { value, error ->
                if (error != null) {
                    failAction(Error(error))
                    return@addSnapshotListener
                }
                if (value != null) {
                    successAction(value.documents.map { CommonDocumentSnapshot(it) })
                } else {
                    failAction(null)
                }

            }
        )
    }

}

actual class CommonFieldValue(
    val ref: FieldValue
) {
    actual companion object {
        actual fun arrayUnion(value: Any): CommonFieldValue {
            return CommonFieldValue(FieldValue.arrayUnion(value))
        }

        actual fun arrayRemove(value: Any): CommonFieldValue {
            return CommonFieldValue(FieldValue.arrayRemove(value))
        }
    }
}

actual class CommonFieldPath(
    val ref: FieldPath
) {
    actual companion object {
        actual fun documentId(): CommonFieldPath = CommonFieldPath(FieldPath.documentId())
    }
}

actual class CommonListenerRegistration(
    val ref: ListenerRegistration
) {
    actual fun remove() = ref.remove()
}