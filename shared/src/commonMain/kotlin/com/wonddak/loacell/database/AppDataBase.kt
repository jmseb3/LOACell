package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.Character
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.api.LostArkApi
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class AppDataBase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()
    private val api = LostArkApi()

    private val raidTypeAdapter = object : ColumnAdapter<RaidType, String> {
        override fun decode(databaseValue: String): RaidType {
            RaidType.values().forEach {
                if (it.name == databaseValue) {
                    return it
                }
            }
            return RaidType.ETC
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

    val roomInfoQueriesHelper = RoomInfoQueriesHelper(database.roomInfoQueries)
    val raidInfoQueriesHelper = RaidInfoQueriesHelper(database.raidInfoQueries)
    suspend fun addUserAndCharacters(roomId: Long, user: String, representativeCharacter: String) {
        database.apply {
            userInfoQueries.insertUserInfo(roomId, user, representativeCharacter)
            api.getCharacterInfo(representativeCharacter).forEach {
                characterQueries.insertCharacterInfo(
                    it.characterName,
                    user,
                    representativeCharacter,
                    it.serverName,
                    it.characterClassName,
                    it.itemMaxLevel
                )
            }
        }
    }

    fun getUsersByRoomId(roomId: Long): Flow<List<UserInfo>> {
        return database.userInfoQueries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun getCharacters(representativeCharacter: String) : Flow<List<Character>> {
        return database.characterQueries.selectByRepresentativeCharacter(representativeCharacter).asFlow().mapToList(Dispatchers.Main)
    }

}