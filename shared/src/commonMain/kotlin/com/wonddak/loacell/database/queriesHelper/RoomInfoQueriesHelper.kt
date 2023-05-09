package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RoomInfoQueriesHelper(
    private val queries: RoomInfoQueries
) {

    fun getALl(): Flow<List<RoomInfo>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Main)
    }

    fun getRoomInfoById(id:String) :RoomInfo {
        return queries.selectById(id).executeAsOne()
    }

    fun addRoomInfo(title: String, description: String,uniqueId:String) {
        queries.insertRoomInfo(uniqueId, title, description)
    }

    fun deleteRoomInfo(roomId: String) {
        queries.deleteById(roomId)
    }
}