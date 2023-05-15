package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getALlByRoomId(roomId: String): Flow<List<RaidInfo>> {
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun getAllByRoomIdValue(roomId: String): List<RaidInfo> {
        return queries.selectByRoomId(roomId).executeAsList()
    }

    fun addRaidInfo(
        raidId: String,
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Long,
        endGateNumber: Long,
    ) {
        queries.insertRaidInfo(
            raidId,
            roomId,
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            false
        )
    }

    fun updateRaidInfo(
        raidId: String,
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Long,
        endGateNumber: Long,
        isFinish: Boolean
    ) {
        queries.upadteRaidInfo(
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            isFinish,
            raidId,
            roomId,
        )
    }

    fun delete(
        id: String,
        roomId: String
    ) {
        queries.delete(id, roomId)
    }
}