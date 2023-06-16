package com.wonddak.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.database.model.RaidType
import com.wonddak.database.queriesHelper.CharacterQueriesHelper
import com.wonddak.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.database.queriesHelper.RoomInfoQueriesHelper
import com.wonddak.database.queriesHelper.UserInfoQueriesHelper
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.model.Difficulty
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
            party2characterListAdapter = stringListAdapter
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

    /**
     * 타입에 맞고 ㅋ
     */
    fun getUsersByRoomIdFilterCharacterAndType(
        roomId: String,
        raidInfo: RaidInfo
    ): Flow<List<UserInfo>> {
        // id에 맞는 레이드 정보 리스트를 가져옴
        val raidList = raidInfoQueriesHelper.getAllByRoomIdValue(roomId)

        //현재 레이드 정보에 들어가있는 캐릭터 이름을 가져옴
        val characterNameInParty = mutableListOf<String>()
        characterNameInParty.addAll(raidInfo.party1characterList.filter { it.isNotEmpty() })
        characterNameInParty.addAll(raidInfo.party2characterList.filter { it.isNotEmpty() })

        //현재 레이드 타입에 맞는 것만 필터링 한뒤 파티에 가입된 캐릭터를 모두 추가한다.
        val totalNameList = mutableListOf<String>()
        raidList
            .filter { it.type == raidInfo.type }
            .forEach {
                //각 레이드 정보에있는 캐릭터 이름을 모두 넣는다.
                totalNameList.addAll(it.party1characterList.filter { it.isNotEmpty() })
                totalNameList.addAll(it.party2characterList.filter { it.isNotEmpty() })
            }

        return database.userInfoQueries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
            .transform {
                //현재 방에 있는 유저 정보를 모두 가져온 뒤
                try {
                    val filter = it.filter { userInfo ->
                        var result = true
                        //캐릭터 이름이 들어가지 않은 유저만 필터랑 하여 내보낸다.
                        for (characterName in characterNameInParty) {
                            val characterList = characterQueriesHelper.getAllList(userInfo)
                            if(characterList.map { it.name }.contains(characterName)) {
                                result = false
                                break
                            }
                        }
                        result
                    }
                    emit(filter)
                }catch (e:Exception) {
                    println("JWH $e")
                    emit(emptyList())
                }
            }
    }

    fun clearAll() {
        database.roomInfoQueries.deleteAll()
    }
}