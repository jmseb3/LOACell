package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.Character
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.api.model.CharacterInfo
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class AppDataBase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()

    private val raidTypeAdapter = object : ColumnAdapter<RaidType, String> {
        override fun decode(databaseValue: String): RaidType {
            return when (databaseValue) {
                RaidType.VALTAN.name -> RaidType.VALTAN
                RaidType.VYKAS.name -> RaidType.VYKAS
                RaidType.KOUKU.name -> RaidType.KOUKU
                RaidType.ABRELSHUD.name -> RaidType.ABRELSHUD
                RaidType.ILLIALAN.name -> RaidType.ILLIALAN
                else -> RaidType.NONE
            }
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
    fun addUserAndCharacters(roomId:Long,user:String,characterList : List<CharacterInfo>) {
        database.apply {
            userInfoQueries.insertUserInfo(user,roomId)
            characterQueries.transaction {
                characterList.forEach { info ->
                    database.characterQueries.insertCharacterInfo(
                        info.characterName,
                        user,
                        info.serverName,
                        info.characterClassName,
                        info.itemMaxLevel
                    )
                }
            }
        }
    }

    fun getUsersByRoomId(roomId: Long) : Flow<List<UserInfo>> {
        return database.userInfoQueries.selectByRoomId(roomId).asFlow().mapToList(Dispatchers.Main)
    }

    fun getCharactersByUser(username: String) : Flow<List<Character>> {
        return database.characterQueries.selectByUserName(username).asFlow().mapToList(Dispatchers.Main)
    }

}