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

    fun addRoomInfo(title: String, description: String, uniqueId: String, owner: String) {
        queries.insertRoomInfo(uniqueId, title, description,owner)
    }

    fun updateRoomInfo(title: String, description: String, owner: String,uniqueId: String) {
        queries.updateInfo(title, description, owner,uniqueId)
    }

    fun deleteRoomInfo(roomId: String) {
        queries.deleteById(roomId)
    }
}