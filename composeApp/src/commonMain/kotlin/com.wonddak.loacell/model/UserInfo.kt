package com.wonddak.loacell.model

import kotlinx.datetime.Clock

data class UserInfo(
    val name: String,
    val roomId: String,
    val representativeCharacter: String,
    val timeStamp: Int,
) {
    fun checkTimeOver(): Boolean {
        val nowTime = Clock.System.now().toEpochMilliseconds()
        return (nowTime - this.timeStamp) / 1000 >= 3_600
    }
}