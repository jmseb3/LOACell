package com.wonddak.loacell.util

import java.text.DecimalFormat

actual object TimeHelper {
    actual fun makeTimeText(hour: Int, minute: Int): String {
        val df = DecimalFormat("00")
        return "${df.format(hour)} : ${df.format(minute)}"
    }
}