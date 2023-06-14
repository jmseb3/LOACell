package com.wonddak.loacell.store

expect class Error {
    val errorMsg :String
}

expect fun getFireStore(): CommonFireStore

expect class CommonFireStore {
    fun collection(path: String): CommonCollection
    fun runTransaction(
        refDoc: CommonDocument,
        successAction: () -> Unit,
        failAction: (error: Error) -> Unit,
        action: (transaction: CommonDocumentSnapshot) -> Unit
    )
}

expect class CommonCollection {
    val id: String
    val parent: CommonDocument?

    fun document(): CommonDocument
    fun document(documentPath: String): CommonDocument

    fun getListenerRegistration(
        successAction: (a:CommonQuerySnapshot) -> Unit,
        failAction: (error: Error?) -> Unit
    ) : CommonListenerRegistration

    fun where(filter :CommonFilter) : CommonQuery
    fun whereIn(filed: String,list:List<Any>): CommonQuery
    fun whereIn(filed: CommonFieldPath,list:List<Any>): CommonQuery
}

expect class CommonFilter {
    companion object {
        fun or(vararg filters:CommonFilter) : CommonFilter
        fun equalTo(filed:String,value: Any) : CommonFilter
        fun arrayContains(filed:String,value: Any) : CommonFilter
    }

}

expect class CommonDocument {
    val id: String
    val path: String
    val parent: CommonCollection

    fun collection(collectionPath: String): CommonCollection

    fun getListenerRegistration(
        successAction: (a:CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error?) -> Unit
    ) : CommonListenerRegistration

    fun set(data: Map<String, Any>)
    fun set(data: Map<String, Any>, successAction: () -> Unit, failAction: (error:Error) -> Unit)

    fun update(data: Map<String, Any>)
    fun update(field: String,value :Any)

    fun update(data: Map<String, Any>, successAction: () -> Unit, failAction: (error:Error) -> Unit)
    fun update(field: String,value :Any,successAction: () -> Unit, failAction: (error:Error) -> Unit)
    fun update(field: String,value :CommonFieldValue,successAction: () -> Unit, failAction: (error:Error) -> Unit)
    fun delete()
    fun delete(successAction: () -> Unit, failAction: (error :Error) -> Unit)

    fun get(
        successAction: (documentSnapshot: CommonDocumentSnapshot) -> Unit,
        failAction: (error: Error) -> Unit
    )
}

expect class CommonDocumentSnapshot {
    val exist :Boolean
    val id: String
    val reference: CommonDocument
    val data : Map<String,Any>?
}

expect class CommonQuerySnapshot {
    val documents : List<CommonDocumentSnapshot>
}

expect class CommonQuery {
    fun get(successAction: (querySnapshot : CommonQuerySnapshot) -> Unit, failAction: (error :Error) -> Unit)
}

expect class CommonFieldValue{
    companion object {
        fun arrayUnion(value :Any) : CommonFieldValue
        fun arrayRemove(value :Any) : CommonFieldValue
    }
}

expect class CommonFieldPath{
    companion object {
        fun documentId() : CommonFieldPath
    }
}

expect class CommonListenerRegistration{
    fun remove()
}