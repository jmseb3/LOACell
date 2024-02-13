package com.wonddak.database.ext

import com.wonddak.loacell.UserInfo
import kotlinx.datetime.Clock

fun UserInfo.checkTimeOver(): Boolean {
    val nowTime = Clock.System.now().toEpochMilliseconds()
    return (nowTime - this.timeStamp!!) / 1000 >= 3_600
}