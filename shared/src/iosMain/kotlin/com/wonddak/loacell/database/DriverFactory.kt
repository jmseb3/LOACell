package com.wonddak.loacell.database

import com.wonddak.loacell.Database
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, DB_NAME).also {
            it.execute(null, "PRAGMA foreign_keys=ON", 0)
        }
    }
}