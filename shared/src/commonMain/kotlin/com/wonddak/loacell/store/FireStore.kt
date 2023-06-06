package com.wonddak.loacell.store

expect class Error {
    val errorMsg :String
}

expect fun getFireStore(): CommonFireStore

expect class CommonFireStore {
    fun collection(path: String): CommonCollection
}

expect class CommonCollection {
    val id: String
    val parent: CommonDocument?

    fun document(): CommonDocument
    fun document(documentPath: String): CommonDocument

}

expect class CommonDocument {
    val id: String
    val path: String
    val parent: CommonCollection

    fun collection(collectionPath: String): CommonCollection

    fun set(data: Map<String, Any>)
    fun set(data: Map<String, Any>, successAction: () -> Unit, failAction: (error:Error) -> Unit)

    fun update(data: Map<String, Any>)
    fun update(field: String,value :Any)

    fun update(data: Map<String, Any>, successAction: () -> Unit, failAction: (error:Error) -> Unit)
    fun update(field: String,value :Any,successAction: () -> Unit, failAction: (error:Error) -> Unit)

    fun delete()
    fun delete(successAction: () -> Unit, failAction: (error :Error) -> Unit)

//    suspend fun get() : CommonDocumentSnapshot
}

expect class CommonDocumentSnapshot {
    val exist :Boolean
    val id: String
    val reference: CommonDocument
    val data : Map<String,Any>?
}
