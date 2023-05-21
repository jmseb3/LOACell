package com.wonddak.database

import app.cash.sqldelight.ColumnAdapter
import com.wonddak.loacell.Database
import com.wonddak.loacell.DriverFactory
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
import com.wonddak.loacell.queriesHelper.CharacterInfoQueriesHelper
import com.wonddak.loacell.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.queriesHelper.RoomInfoQueriesHelper
import com.wonddak.loacell.queriesHelper.UserInfoQueriesHelper

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
}