package com.wonddak.loacell.database.queriesHelper

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RaidInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class RaidInfoQueriesHelper(
    private val queries: RaidInfoQueries
) {
    fun getALlByRoomId(roomId: String): Flow<List<RaidInfo>> {
        return queries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

//    fun addRaidInfo(
//        raidId:String,
//        roomId: String,
//        title: String,
//        type: RaidType,
//        difficulty: Difficulty,
//        gateNumber :Long,
//    ) {
//        queries.insertRaidInfo(raidId, roomId, title, type,difficulty,gateNumber,false)
//    }

    fun delete(id:String) {
        queries.delete(id)
    }
}