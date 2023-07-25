package com.wonddak.loacell.util

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

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
        return "${NSString.stringWithFormat("%02d",hour)} : ${NSString.stringWithFormat("%02d",minute)}"
    }
}