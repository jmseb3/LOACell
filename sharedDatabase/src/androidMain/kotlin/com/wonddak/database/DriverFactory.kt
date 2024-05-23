package com.wonddak.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.wonddak.loacell.Database
import org.koin.java.KoinJavaComponent

actual class DriverFactory{
    private val context : Context = KoinJavaComponent.getKoin().get()
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            Database.Schema,
            context,
            DB_NAME,
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    super.onConfigure(db)
                    db.setForeignKeyConstraintsEnabled(true)
                }
            })
    }
}