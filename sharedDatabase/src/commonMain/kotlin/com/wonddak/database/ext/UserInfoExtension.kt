package com.wonddak.database.ext

import com.wonddak.loacell.Character
import com.wonddak.loacell.UserInfo

fun UserInfo.checkTimeOver(nowTime: Long): Boolean {
    return (nowTime - this.timeStamp!!) / 1000 >= 3_600
}