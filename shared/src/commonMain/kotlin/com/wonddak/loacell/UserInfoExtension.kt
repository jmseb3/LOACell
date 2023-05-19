package com.wonddak.loacell

fun UserInfo.checkTimeOver(nowTime: Long): Boolean {
    return (nowTime - this.timeStamp!!) / 1000 >= 3_600
}