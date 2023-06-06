package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.model.CharacterInfo
import korlibs.time.DateTime

data class FBCharacter(
    val level: String = "",
    val server: String = "",
    val className: String = "",
    val timeStamp: Long = DateTime.now().milliseconds.toLong(),
) {
    fun toMap(): Map<Any?, Any> {
        return mapOf(
            "level" to level,
            "server" to server,
            "className" to className,
            "timeStamp" to timeStamp
        )
    }
}

expect object CharacterHelper {
    fun addOrUpdate(
        character: CharacterInfo,
        db: AppDataBase
    )

    fun get(
        name :String,
        db: AppDataBase
    )
}