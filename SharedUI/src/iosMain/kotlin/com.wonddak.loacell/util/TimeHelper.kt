package com.wonddak.loacell.util

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual object TimeHelper {
    actual fun makeTimeText(hour: Int, minute: Int): String {
        return "${NSString.stringWithFormat("%02d",hour)} : ${NSString.stringWithFormat("%02d",minute)}"
    }
}