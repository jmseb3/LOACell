package com.wonddak.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RoomInfoQueriesHelper(
    private val queries: RoomInfoQueries
) {

    fun getAll(): Flow<List<RoomInfo>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Main)
    }

    fun getRoomInfoById(id: String): Flow<RoomInfo> {
        return queries.selectById(id).asFlow().mapToOne(Dispatchers.Default)
    }

    fun addRoomInfo(
        title: String,
        description: String,
        uniqueId: String,
        owner: String,
        enterUser: List<String> ,
        editableUser: List<String>,
    ) {
        queries.insertRoomInfo(
            uniqueId,
            title,
            description,
            owner,
            enterUser,
            editableUser,
        )
    }

    fun updateRoomInfo(
        title: String,
        description: String,
        owner: String,
        enterUser: List<String>,
        editableUser: List<String>,
        uniqueId: String
    ) {
        queries.updateInfo(
            title,
            description,
            owner,
            enterUser,
            editableUser,
            uniqueId
        )
    }
    fun getAllRoomListByOwnerId(
        ownerId :String
    ) : List<RoomInfo> {
        return queries.selectByOwner(ownerId).executeAsList()
    }
    fun deleteRoomInfo(roomId: String) {
        if (roomId.isNotEmpty()) {
            queries.deleteById(roomId)
        }
    }
}