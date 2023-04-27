package com.wonddak.loacell.database

import app.cash.sqldelight.ColumnAdapter
import com.wonddak.loacell.Database
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.queriesHelper.RaidInfoQueriesHelper
import com.wonddak.loacell.database.queriesHelper.RoomInfoQueriesHelper

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

}