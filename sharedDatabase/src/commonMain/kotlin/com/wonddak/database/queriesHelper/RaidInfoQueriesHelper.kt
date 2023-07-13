package com.wonddak.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wonddak.database.model.Day
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import com.wonddak.loacell.model.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getAllByRoomId(roomId: String): Flow<List<RaidInfo>> {
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun getAllByRoomIdValue(roomId: String): List<RaidInfo> {
        return queries.selectByRoomId(roomId).executeAsList()
    }

    fun getRaidInfoById(roomId: String,raidId: String): Flow<RaidInfo?> {
        return queries.selectByRaidId(roomId,raidId).asFlow()
            .mapToOneOrNull(Dispatchers.Main)
    }

    fun addRaidInfo(
        raidId: String,
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Long,
        endGateNumber: Long,
        party1 : List<String>,
        party2 : List<String>,
        day :Day,
        hour :Long,
        minute :Long
    ) {
        queries.insertRaidInfo(
            raidId,
            roomId,
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            false,
            party1,
            party2,
            day,
            hour,
            minute
        )
    }
    fun addRaidInfo(
        raidId: String,
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Long,
        endGateNumber: Long,
        party1 : List<String>,
        party2 : List<String>
    ) {
        queries.insertRaidInfo(
            raidId,
            roomId,
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            false,
            party1,
            party2,
            Day.NONE,
            0,
            0
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
        isFinish: Boolean,
        party1 : List<String>,
        party2 : List<String>
    ) {
        queries.updateRaidInfo(
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            isFinish,
            party1,
            party2,
            raidId,
            roomId
        )
    }

    fun delete(
        id: String,
        roomId: String
    ) {
        queries.delete(id, roomId)
    }
}