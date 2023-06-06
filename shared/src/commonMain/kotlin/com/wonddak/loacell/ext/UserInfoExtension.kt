package com.wonddak.loacell.ext

import com.wonddak.loacell.UserInfo

fun UserInfo.checkTimeOver(nowTime: Long): Boolean {
    return (nowTime - this.timeStamp!!) / 1000 >= 3_600
}