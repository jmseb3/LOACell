package com.wonddak.loacell.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
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

    fun getRaidInfoById(roomId: String,raidId: String): Flow<RaidInfo> {
        return queries.selectByRaidId(roomId,raidId).asFlow()
            .mapToOne(Dispatchers.Main)
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