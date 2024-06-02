package com.wonddak.database

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory(){
    fun createDriver(): SqlDriver
}
internal const val DB_NAME = "test.db"