package com.wonddak.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.database.ext.getMinLevel
import com.wonddak.database.model.Day
import com.wonddak.database.model.Difficulty
import com.wonddak.database.model.RaidType
import com.wonddak.database.queriesHelper.CharacterQueriesHelper
import com.wonddak.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.database.queriesHelper.RoomInfoQueriesHelper
import com.wonddak.database.queriesHelper.UserInfoQueriesHelper
import com.wonddak.loacell.Character
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
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
            if (databaseValue == "") {
                return emptyList()
            }
            return databaseValue.split("^^")
        }

        override fun encode(value: List<String>): String {
            return value.joinToString("^^")
        }
    }

    private val database = Database(
        driver = driver,
        RaidInfoAdapter = RaidInfo.Adapter(
            typeAdapter = raidTypeAdapter,
            DifficultyAdapter = difficultyTypeAdapter,
            party1characterListAdapter = stringListAdapter,
            party2characterListAdapter = stringListAdapter,
            dayAdapter = object : ColumnAdapter<Day,Long> {
                override fun decode(databaseValue: Long): Day {
                    Day.values().forEach {
                        if (it.index.toLong() == databaseValue) {
                            return  it
                        }
                    }
                    return  Day.NONE
                }

                override fun encode(value: Day): Long {
                    return value.index.toLong()
                }

            }
        ),
        RoomInfoAdapter = RoomInfo.Adapter(
            enterUserAdapter = stringListAdapter,
            editableUserAdapter = stringListAdapter
        )
    )

    val roomInfoQueriesHelper = RoomInfoQueriesHelper(database.roomInfoQueries)
    val raidInfoQueriesHelper = RaidInfoQueriesHelper(database.raidInfoQueries)
    val userInfoQueriesHelper = UserInfoQueriesHelper(database.userInfoQueries)
    val characterQueriesHelper = CharacterQueriesHelper(database.characterQueries)

    fun getUsersByRoomIdFilterCharacterAndType(
        roomId: String,
        raidInfo: RaidInfo //현재 레이드 정보
    ): Flow<Map<String,List<Character>>> {
        // id에 맞는 레이드 정보 리스트를 가져옴
        val raidList = raidInfoQueriesHelper.getAllByRoomIdValue(roomId)

        //현재 레이드 정보에 들어가있는 캐릭터 이름을 가져옴
        val characterNameInParty = mutableListOf<String>()
        characterNameInParty.addAll(raidInfo.party1characterList.filter { it.isNotEmpty() })
        characterNameInParty.addAll(raidInfo.party2characterList.filter { it.isNotEmpty() })

        //현재 레이드 타입에 맞는 것만 필터링 한뒤 파티에 가입된 캐릭터를 모두 추가한다.
        val totalNameList = mutableSetOf<String>()
        raidList
            .filter { it.type == raidInfo.type }
            .forEach {
                //각 레이드 정보에있는 캐릭터 이름을 모두 넣는다.
                totalNameList.addAll(it.party1characterList.filter { it.isNotEmpty() })
                totalNameList.addAll(it.party2characterList.filter { it.isNotEmpty() })
            }

        return database.userInfoQueries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
            .transform { userInfoList ->
                //모든 유저 정보를 가져온다.
                try {
                    val result :MutableMap<String,List<Character>> = mutableMapOf()
                    userInfoList.forEach { userInfo ->
                        var find = true
                        val characterList :List<Character> = characterQueriesHelper.getAllListByLevelFilter(userInfo,raidInfo.getMinLevel())

                        //현재 레이드 정보에 캐릭터가 들어가 있는 사람은 제외시킨다.
                        for (characterName in characterNameInParty) {
                            if(characterList.map { it.name }.contains(characterName)) {
                                find = false
                                break
                            }
                        }

                        if (find) {
                            val newList = characterList.filter { !totalNameList.contains(it.name) }
                            if (newList.isNotEmpty()){
                                result[userInfo.name] = newList
                            }
                        }
                    }
                    emit(result)
                }catch (e:Exception) {
                    println("JWH $e")
                    emit(mapOf())
                }
            }
    }

    fun clearAll() {
        database.roomInfoQueries.deleteAll()
    }
}