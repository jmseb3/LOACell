package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.room.RaidInfo
import com.wonddak.loacell.room.RaidInfoQueries
import com.wonddak.loacell.room.RoomInfo
import com.wonddak.loacell.room.RoomInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getALlByRoomId(roomId: Long): Flow<List<RaidInfo>> {
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun addRaidInfo(roomId: Long, title: String, type: RaidType = RaidType.NONE) {
        queries.insertRaidInfo(null, roomId, title, type)
    }

    fun delete(id:Long) {
        queries.delete(id)
    }
}