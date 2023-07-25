package com.wonddak.loacell.util

expect object TimeHelper {

    fun makeTimeText(
        hour: Int,
        minute: Int
    ): String

    fun makeTimeText(
        hour: Int,
        minute: Int,
        step: Int
    ): String
}
