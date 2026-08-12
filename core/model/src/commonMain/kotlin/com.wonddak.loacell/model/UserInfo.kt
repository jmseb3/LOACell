package com.wonddak.loacell.model

import kotlin.time.Clock

data class UserInfo(
    val name: String,
    val roomId: String,
    val representativeCharacter: String,
    val timeStamp: Long,
    val characterList: List<Character>,
) {
    fun checkTimeOver(): Boolean =
        (Clock.System.now().toEpochMilliseconds() - timeStamp) / 1000 >= 3_600

    val characterNameList: Set<String>
        get() = characterList.map { it.name }.toSet()
}

data class Character(
    val name: String,
    val server: String,
    val className: String,
    val level: String,
) {
    fun getLevel(): Float = level.replace(",", "").toFloat()
}
