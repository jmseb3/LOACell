package com.wonddak.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.database.model.Day
import com.wonddak.database.model.Difficulty
import com.wonddak.database.model.RaidType
import com.wonddak.database.model.convertToDay
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getAllByRoomId(roomId: String): Flow<List<RaidInfo>> {
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.IO)
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
        party1: List<String>,
        party2: List<String>,
        party3: List<String> = emptyList(),
        party4: List<String> = emptyList(),
        day: Day = Day.NONE,
        hour: Long = 0,
        minute: Long = 0
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
            party3,
            party4,
            day,
            hour,
            minute
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
        party1: List<String>,
        party2: List<String>,
        party3: List<String>,
        party4: List<String>,
        dayIndex :Long?,
        hour :Long?,
        minute: Long?
    ) {
        val day = dayIndex?.convertToDay() ?: Day.NONE
        queries.updateRaidInfo(
            title,
            type,
            difficulty,
            startGateNumber,
            endGateNumber,
            isFinish,
            party1,
            party2,
            party3,
            party4,
            day,
            hour ?:0L,
            minute ?: 0L,
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