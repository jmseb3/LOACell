package com.wonddak.loacell.util

expect object TimeHelper {
    fun makeTimeText(
        hour: Int,
        minute: Int
    ): String
}

fun TimeHelper.makeTimeText(
    hour: Int,
    minute: Int,
    step: Int
): String {
    val timeTotal = hour * 60 + minute + step
    val newHour = timeTotal / 60
    val newMinute = timeTotal % 60
    return "${makeTimeText(hour, minute)}\n~\n${makeTimeText(newHour, newMinute)}"
}