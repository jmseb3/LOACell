package com.wonddak.loacell.model

import com.wonddak.loacell.store.CommonDocumentSnapshot
import kotlinx.datetime.Clock

object UserInfoField {
    internal const val REPRESENTATIVE_CHARACTER = "representativeCharacter"
    internal const val TIME_STAMP = "timeStamp"
    internal const val CHARACTER_LIST = "characterList"
    internal const val NAME = "name"
    internal const val SERVER = "server"
    internal const val CLASS_NAME = "className"
    internal const val LEVEL = "level"
}

data class UserInfo(
    val name: String,
    val roomId: String,
    val representativeCharacter: String,
    val timeStamp: Int,
    val characterList: List<Character>,
) {
    fun checkTimeOver(): Boolean {
        val nowTime = Clock.System.now().toEpochMilliseconds()
        return (nowTime - this.timeStamp) / 1000 >= 3_600
    }
}

data class Character(
    val name: String,
    val server: String,
    val className: String,
    private val level: String,
) {
    fun getLevel(): Float {
        return this.level.replace(",", "").toFloat()
    }
}

fun CommonDocumentSnapshot.toUserInfo(roomId: String): UserInfo {
    return with(this.data!!) {
        UserInfo(
            this@toUserInfo.id,
            roomId,
            this[UserInfoField.REPRESENTATIVE_CHARACTER] as String,
            (this[UserInfoField.TIME_STAMP] as Long).toInt(),
            (this[UserInfoField.CHARACTER_LIST] as List<Map<String, Any>>).map {
                Character(
                    it[UserInfoField.NAME] as String,
                    it[UserInfoField.SERVER] as String,
                    it[UserInfoField.CLASS_NAME] as String,
                    it[UserInfoField.LEVEL] as String
                )
            }.sortedBy { -it.getLevel() }
        )
    }
}