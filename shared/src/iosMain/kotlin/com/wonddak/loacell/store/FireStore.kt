package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRCollectionReference
import cocoapods.FirebaseFirestore.FIRDocumentReference
import cocoapods.FirebaseFirestore.FIRDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRFilter
import cocoapods.FirebaseFirestore.FIRFirestore
import cocoapods.FirebaseFirestore.FIRQuery
import cocoapods.FirebaseFirestore.FIRQueryDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRQuerySnapshot
import platform.Foundation.NSError

actual class Error(error: NSError?) {
    actual val errorMsg: String = error?.localizedDescription ?: "unknown error"

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

    actual fun where(filter: CommonFilter): CommonQuery {
        return CommonQuery(ref.queryWhereFilter(filter.ref))
    }
}

actual class CommonFilter(
    val ref: FIRFilter
) {
    actual companion object {
        actual fun equalTo(
            filed: String,
            value: Any
        ): CommonFilter = CommonFilter(FIRFilter.filterWhereField(filed, isEqualTo = value))

        actual fun arrayContains(
            filed: String,
            value: Any
        ): CommonFilter = CommonFilter(FIRFilter.filterWhereField(filed, arrayContains = value))


        actual fun or(vararg filters: CommonFilter): CommonFilter {
            val convertFilter = filters.map { it.ref }
            return CommonFilter(FIRFilter.orFilterWithFilters(convertFilter))
        }

    }

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

    actual fun get(
        successAction: (documentSnapshot: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.getDocumentWithCompletion { firDocumentSnapshot, nsError ->
            if (nsError == null) {
                if (firDocumentSnapshot != null) {
                    successAction(CommonDocumentSnapshot(firDocumentSnapshot))
                } else {
                    failAction(com.wonddak.loacell.store.Error(null))
                }
            } else {
                failAction(com.wonddak.loacell.store.Error(nsError))
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

actual class CommonQuerySnapshot(
    val ref: FIRQuerySnapshot
) {
    actual val documents: List<CommonDocumentSnapshot> = (ref.documents.filterIsInstance<FIRDocumentSnapshot>()).map { CommonDocumentSnapshot(it) }
}

actual class CommonQuery(
    val ref : FIRQuery
) {

    actual fun get(
        successAction: (querySnapshot: CommonQuerySnapshot) -> Unit,
        failAction: (error: Error) -> Unit
    ) {
        ref.getDocumentsWithCompletion { firQuerySnapshot, nsError ->
            if (nsError == null) {
                if (firQuerySnapshot != null) {
                    successAction(CommonQuerySnapshot(firQuerySnapshot))
                } else {
                    failAction(Error(null))
                }
            } else {
                failAction(Error(nsError))
            }
        }
    }

}