package com.wonddak.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wonddak.database.model.Day
import com.wonddak.database.model.Difficulty
import com.wonddak.database.model.RaidType
import com.wonddak.database.model.convertToDay
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
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

    fun getRaidInfoById(roomId: String, raidId: String): Flow<RaidInfo?> {
        return queries.selectByRaidId(roomId, raidId).asFlow()
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
        party1: List<String>,
        party2: List<String>,
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
        party2: List<String>
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

    fun updateRaidInfoDay(
        raidId: String,
        roomId: String,
        dayIndex: Long,
        hour: Long,
        minute: Long
    ) {
        val day = dayIndex.convertToDay()
        updateRaidInfoDay(raidId, roomId, day, hour, minute)
    }

    fun updateRaidInfoDay(
        raidId: String,
        roomId: String,
        day: Day,
        hour: Long,
        minute: Long
    ) {
        queries.updateDay(
            day,
            hour,
            minute,
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