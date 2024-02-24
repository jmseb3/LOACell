package com.wonddak.loacell.util

import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

fun UIViewController.openName(name :NSString) {
    val url =NSURL(string = KLOA  +name)
    val sf = SFSafariViewController(uRL = url)
    this.presentViewController(viewControllerToPresent = sf,animated = true, completion = null)
}