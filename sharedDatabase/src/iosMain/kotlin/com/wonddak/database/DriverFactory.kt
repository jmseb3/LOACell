package com.wonddak.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.wonddak.loacell.Database

actual class DriverFactory() {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "test.db").also {
            it.execute(null, "PRAGMA foreign_keys=ON", 0)
        }
    }
}