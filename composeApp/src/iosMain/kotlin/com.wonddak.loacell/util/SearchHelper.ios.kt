package com.wonddak.loacell.util

import platform.Foundation.NSString
import platform.Foundation.NSURL

fun makeUrl(base :String, name: NSString): NSURL {
    return NSURL(string = base + name)
}