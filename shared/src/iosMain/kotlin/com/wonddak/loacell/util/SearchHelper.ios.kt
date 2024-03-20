package com.wonddak.loacell.util

import com.wonddak.loacell.KLOA
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

fun makeUrl(base :String, name: NSString): NSURL {
    return NSURL(string = base + name)
}