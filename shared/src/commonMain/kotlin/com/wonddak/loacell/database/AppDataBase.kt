package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.wonddak.loacell.Character
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppDataBase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()

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

    private val difficultyTypeAdapter = object : ColumnAdapter<Difficulty, Long> {
        override fun decode(databaseValue: Long): Difficulty {
            return when (databaseValue) {
                2L -> Difficulty.Hell
                1L -> Difficulty.Hard
                else -> Difficulty.Normal
            }
        }

        override fun encode(value: Difficulty): Long {
            return when (value) {
                Difficulty.Hell -> 2L
                Difficulty.Hard -> 1L
                else -> 0L
            }
        }

    }

    private val stringListAdapter = object  : ColumnAdapter<List<String>,String> {
        override fun decode(databaseValue: String): List<String> {
            return databaseValue.split(",")
        }

        override fun encode(value: List<String>): String {
            return value.joinToString(",")
        }
    }
    private val database = Database(
        driver = driver,
        RaidInfoAdapter = RaidInfo.Adapter(
            typeAdapter = raidTypeAdapter,
            DifficultyAdapter = difficultyTypeAdapter
        ),
        UserInfoAdapter = UserInfo.Adapter(
            characterListAdapter = stringListAdapter
        )
    )

    val roomInfoQueriesHelper = RoomInfoQueriesHelper(database.roomInfoQueries)
    val raidInfoQueriesHelper = RaidInfoQueriesHelper(database.raidInfoQueries)

    fun addUser(
        userString: String,
        roomId: String,
        representativeCharacter: String,
        characterList : List<String>
    ) {
        database.userInfoQueries.insertUserInfo(
            userString,
            roomId,
            representativeCharacter,
            characterList
        )
    }

    fun updateUserInfo(
        userName: String,
        characterList : List<String>,
        roomId: String,
        representativeCharacter: String

    ) {
        database.userInfoQueries.updateUserInfo(
            representativeCharacter,characterList,userName, roomId
        )
    }

    fun getUsersByRoomId(roomId: String): Flow<List<UserInfo>> {
        return database.userInfoQueries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
    }

    fun getUsersByRoomIdValue(roomId: String): List<UserInfo> {
        return database.userInfoQueries.selectByRoomId(roomId).executeAsList()
    }

    fun deleteUserName(user: String, roomId: String) {
        database.userInfoQueries.deleteUserById(user, roomId)
    }

    fun getCharacter(characterName: String) : Flow<Character> {
        return database.characterQueries.getCharacterInfo(characterName).asFlow().mapToOne(Dispatchers.Main)
    }
    fun getCharacterValue(characterName: String) : Character? {
        return try {
            database.characterQueries.getCharacterInfo(characterName).executeAsOne()
        } catch (e:Exception) {
            null
        }
    }

    fun updateCharacter(
        characterName: String,
        server: String,
        className: String,
        level: String
    ) {
        database.characterQueries.insertCharacterInfo(
            characterName,
            server,
            className,
            level
        )
    }
    fun deleteCharacter(characterName: String) {
        database.characterQueries.deleteUserByName(characterName)
    }

}