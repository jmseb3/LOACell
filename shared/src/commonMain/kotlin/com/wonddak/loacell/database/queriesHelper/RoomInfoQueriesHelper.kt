package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.room.RoomInfo
import com.wonddak.loacell.room.RoomInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RoomInfoQueriesHelper(
    private val queries: RoomInfoQueries
) {

    fun getALl(): Flow<List<RoomInfo>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Main)
    }

    fun addRoomInfo(title: String) {
        queries.insertRoomInfo(null, title)
    }

    fun deleteRoomInfo(roomId: Long) {
        queries.deleteById(roomId)
    }
}