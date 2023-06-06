package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase


expect class ListenerDoc {
    fun remove()
}

expect object ObserveHelper {
    fun roomInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc

    fun users(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc

    fun raidInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc
}