package com.wonddak.loacell

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.const.RaidType
import com.wonddak.loacell.room.RaidInfo
import com.wonddak.loacell.room.RoomInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class AppDataBase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()


//    private val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
//        override fun decode(databaseValue: String) =
//            if (databaseValue.isEmpty()) {
//                listOf()
//            } else {
//                databaseValue.split(",")
//            }
//
//        override fun encode(value: List<String>) = value.joinToString(separator = ",")
//    }

    private val raidTypeAdapter = object : ColumnAdapter<RaidType,String> {
        override fun decode(databaseValue: String): RaidType {
            if (databaseValue == RaidType.VALTAN_NORMAL.name) {
                return  RaidType.VALTAN_NORMAL
            } else if (databaseValue == RaidType.VALTAN_HARD.name) {
                return  RaidType.VALTAN_HARD
            }
            return RaidType.VALTAN_NORMAL
        }

        override fun encode(value: RaidType): String {
            return value.name
        }
    }

    private val database = Database(
        driver = driver,
        RaidInfoAdapter = RaidInfo.Adapter(
            typeAdapter = raidTypeAdapter
        )
    )

    private val roomInfoQueries = database.roomInfoQueries
    private val raidInfoQueries = database.raidInfoQueries

    fun getRoomInfo(): Flow<List<RoomInfo>> {
        return roomInfoQueries.selectAll().asFlow().mapToList(Dispatchers.Main)
    }

    fun getRaidInfoFromRoomId(roomId: Long): Flow<List<RaidInfo>> {
        return raidInfoQueries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun addRoomInfo(title: String) {
        roomInfoQueries.insertRoomInfo(null, title)
    }

    fun addRaidInfo(roomId: Long, title: String) {
        raidInfoQueries.insertRaidInfo(null, roomId, title, RaidType.VALTAN_HARD)
    }

    fun deleteRoomInfo(roomId: Long) {
        roomInfoQueries.deleteById(roomId)
    }

}