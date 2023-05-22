package com.wonddak.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.database.queriesHelper.CharacterInfoQueriesHelper
import com.wonddak.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.database.queriesHelper.UserInfoQueriesHelper
import com.wonddak.loacell.Database
import com.wonddak.loacell.DriverFactory
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
import com.wonddak.loacell.queriesHelper.RoomInfoQueriesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

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
            DifficultyAdapter = difficultyTypeAdapter,
            party1characterListAdapter = stringListAdapter,
            party2characterListAdapter = stringListAdapter
        ),
        UserInfoAdapter = UserInfo.Adapter(
            characterListAdapter = stringListAdapter
        )
    )

    val roomInfoQueriesHelper = RoomInfoQueriesHelper(database.roomInfoQueries)
    val raidInfoQueriesHelper = RaidInfoQueriesHelper(database.raidInfoQueries)
    val userInfoQueriesHelper = UserInfoQueriesHelper(database.userInfoQueries)
    val characterInfoQueriesHelper = CharacterInfoQueriesHelper(database.characterQueries)

    fun getUsersByRoomIdFilterCharacterAndType(
        roomId: String,
        raidInfo: RaidInfo
    ): Flow<List<UserInfo>> {
        val raidList = database.raidInfoQueries.selectByRoomId(roomId).executeAsList()

        val nameInPartyList = mutableListOf<String>()
        nameInPartyList.addAll(raidInfo.party1characterList.filter { it.isNotEmpty() })
        nameInPartyList.addAll(raidInfo.party2characterList.filter { it.isNotEmpty() })

        val totalNameList = mutableListOf<String>()
        raidList.filter { it.type == raidInfo.type }.forEach {
            totalNameList.addAll(it.party1characterList.filter { it.isNotEmpty() })
            totalNameList.addAll(it.party2characterList.filter { it.isNotEmpty() })
        }

        return database.userInfoQueries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
            .transform {
                try {
                    val filter = it.filter { userInfo ->
                        var result = true
                        for (name in nameInPartyList) {
                            if (userInfo.characterList.contains(name)) {
                                result = false
                                break
                            }
                        }
                        result
                    }
                    val result = filter.map {
                        UserInfo(
                            it.name,
                            it.roomId,
                            it.representativeCharacter,
                            it.characterList.toMutableList().filter { name ->!totalNameList.contains(name) },
                            it.timeStamp
                        )
                    }
                    emit(result)
                }catch (e:Exception) {
                    println("JWH $e")
                    emit(emptyList())
                }
            }
    }
}