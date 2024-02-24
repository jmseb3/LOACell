package com.wonddak.loacell.util

import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

fun makeUrl(name: NSString): NSURL {
    return NSURL(string = KLOA + name)
}