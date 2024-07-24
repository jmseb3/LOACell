package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import com.wonddak.loacell.database.model.Day
import com.wonddak.loacell.database.model.Difficulty
import com.wonddak.loacell.database.model.RaidType
import com.wonddak.loacell.database.queriesHelper.CharacterQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.UserInfoQueriesHelper
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo

class AppDataBase(driverFactory: DriverFactory) {
    private val driver = driverFactory.createDriver()

    private val raidTypeAdapter = object : ColumnAdapter<RaidType, String> {
        override fun decode(databaseValue: String): RaidType {
            RaidType.entries.forEach {
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
                4L -> Difficulty.ExtremeHard
                3L -> Difficulty.ExtremeNormal
                2L -> Difficulty.Hell
                1L -> Difficulty.Hard
                else -> Difficulty.Normal
            }
        }

        override fun encode(value: Difficulty): Long {
            return when (value) {
                Difficulty.ExtremeHard -> 4L
                Difficulty.ExtremeNormal -> 3L
                Difficulty.Hell -> 2L
                Difficulty.Hard -> 1L
                else -> 0L
            }
        }

    }

    private val stringListAdapter = object : ColumnAdapter<List<String>, String> {
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
            party3characterListAdapter = stringListAdapter,
            party4characterListAdapter = stringListAdapter,
            dayAdapter = object : ColumnAdapter<Day, Long> {
                override fun decode(databaseValue: Long): Day {
                    Day.entries.forEach {
                        if (it.index.toLong() == databaseValue) {
                            return it
                        }
                    }
                    return Day.NONE
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
    fun clearAll() {
        database.roomInfoQueries.deleteAll()
    }
}