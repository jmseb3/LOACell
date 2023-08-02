package com.wonddak.loacell.util

import java.text.DecimalFormat

actual object TimeHelper {

    actual fun makeTimeText(
        hour: Int,
        minute: Int,
        step: Int
    ): String {
        val timeTotal = hour * 60 + minute + step
        val newHour = timeTotal / 60
        val newMinute = timeTotal % 60
        return "${makeTimeText(hour, minute)}\n~\n${makeTimeText(newHour, newMinute)}"
    }

    actual fun makeTimeText(hour: Int, minute: Int): String {
        val df = DecimalFormat("00")
        return "${df.format(hour)} : ${df.format(minute)}"
    }

}