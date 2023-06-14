package com.wonddak.loacell.store

object RefHelper {
    fun getRoomsRef(): CommonCollection = getFireStore().collection("rooms")
    fun getRoomRef(id: String): CommonDocument = getRoomsRef().document(id)

    fun getRaidsRef(roomId: String): CommonCollection = getRoomRef(roomId).collection("raidInfo")
    fun getRaidRef(roomId: String, raidId: String): CommonDocument = getRaidsRef(roomId).document(raidId)

    fun getUsersRef(roomId: String): CommonCollection = getRoomRef(roomId).collection("users")
    fun getUserDocRef(roomId: String, name: String): CommonDocument = getUsersRef(roomId).document(name)
}