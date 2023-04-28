package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import com.wonddak.loacell.database.const.RaidType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getALlByRoomId(roomId: Long): Flow<List<RaidInfo>> {
        if (roomId <= 0L) {
            return flow { emit(emptyList()) }
        }
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun addRaidInfo(roomId: Long, title: String, type: RaidType = RaidType.ETC) {
        queries.insertRaidInfo(null, roomId, title, type)
    }

    fun delete(id:Long) {
        queries.delete(id)
    }
}