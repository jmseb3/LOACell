package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.Character
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.api.LostArkApi
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

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

    private val difficultyTypeAdapter = object : ColumnAdapter<Difficulty, Long> {
        override fun decode(databaseValue: Long): Difficulty {
            return when(databaseValue) {
                2L -> Difficulty.Hell
                1L -> Difficulty.Hard
                else -> Difficulty.Normal
            }
        }

        override fun encode(value: Difficulty): Long {
            return when(value) {
                Difficulty.Hell -> 2L
                Difficulty.Hard -> 1L
                else -> 0L
            }
        }

    }

    private val database = Database(
        driver = driver,
        RaidInfoAdapter = RaidInfo.Adapter(
            typeAdapter = raidTypeAdapter,
            DifficultyAdapter = difficultyTypeAdapter
        )
    )

    val roomInfoQueriesHelper = RoomInfoQueriesHelper(database.roomInfoQueries)
    val raidInfoQueriesHelper = RaidInfoQueriesHelper(database.raidInfoQueries)
    suspend fun addUserAndCharacters(
        roomId: Long,
        user: String,
        representativeCharacter: String
    ): Boolean {
        val result = database.characterQueries.checkAlreadyExist(representativeCharacter).executeAsOne()
        if (result) {
            return false
        }
        val characterList = api.getCharacterInfo(representativeCharacter)
        database.apply {
            userInfoQueries.insertUserInfo(null, roomId, user, representativeCharacter)
            userInfoQueries.lastInsertRowId().executeAsOne().let { userId ->
                characterList.forEach {
                    characterQueries.insertCharacterInfo(
                        it.characterName,
                        userId,
                        it.serverName,
                        it.characterClassName,
                        it.itemMaxLevel
                    )
                }
            }
        }
        return true
    }
    fun updateUserRepresentativeCharacter(
        userId: Long,
        representativeCharacter: String
    ) {
        database.userInfoQueries.upadteRepresentativeCharacter(
            representativeCharacter, userId
        )
    }

    fun getUsersByRoomId(roomId: Long): Flow<List<UserInfo>> {
        return database.userInfoQueries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
    }

    fun getCharacters(userId: Long): Flow<List<Character>> {
        return database.characterQueries.selectByuserId(userId).asFlow()
            .mapToList(Dispatchers.Main).transform {
                emit(it.sortedByDescending { it.level.replace(",", "").toFloat()  })
            }
    }

    suspend fun updateUserCharacters(roomId: Long) : Flow<String> = flow{
        database.userInfoQueries.selectByRoomId(roomId).executeAsList().forEach { userInfo ->
            val characterList = api.getCharacterInfo(userInfo.representativeCharacter)
            emit(userInfo.name)
            database.transaction {
                characterList.forEach { characterInfo ->
                    database.characterQueries.insertCharacterInfo(
                        characterInfo.characterName,
                        userInfo.userId,
                        characterInfo.serverName,
                        characterInfo.characterClassName,
                        characterInfo.itemMaxLevel
                    )
                }
            }
            delay(500)
        }
        delay(1_000)
        emit("finish")
    }

    fun deleteUserById(userId: Long) {
        database.userInfoQueries.deleteUserById(userId)
    }

}