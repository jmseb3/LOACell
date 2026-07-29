package com.wonddak.loacell.model

import kotlin.time.Clock

object UserInfoField {
    const val REPRESENTATIVE_CHARACTER = "representativeCharacter"
    const val TIME_STAMP = "timeStamp"
    const val CHARACTER_LIST = "characterList"
    const val NAME = "name"
    const val SERVER = "server"
    const val CLASS_NAME = "className"
    const val LEVEL = "level"
}

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
    private val level: String,
) {
    fun getLevel(): Float = level.replace(",", "").toFloat()
}
