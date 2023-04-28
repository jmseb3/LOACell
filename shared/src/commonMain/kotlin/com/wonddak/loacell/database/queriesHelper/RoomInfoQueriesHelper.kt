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

    fun getRoomInfoById(id:Long) :RoomInfo {
        if (id <= 0L) {
            return RoomInfo(0,"","","")
        }
        return queries.selectById(id).executeAsOne()
    }

    fun addRoomInfo(title: String, description: String,uniqueId:String) {
        queries.insertRoomInfo(null, title, description,uniqueId)
    }

    fun deleteRoomInfo(roomId: Long) {
        queries.deleteById(roomId)
    }
}