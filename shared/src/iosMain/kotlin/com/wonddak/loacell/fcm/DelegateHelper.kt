package com.wonddak.loacell.fcm

import cocoapods.FirebaseMessaging.FIRMessaging
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol

/**
 * Helper object for Ios AppDelegate Setting
 */
object DelegateHelper {

    enum class NotificationType(val value: UInt) {
        Badge(1u.shl(0)),
        Sound(1u.shl(1)),
        Alert(1u.shl(2)),
    }

    // https://firebase.google.com/docs/cloud-messaging/ios/client?_gl=1*1umuc6b*_up*MQ..*_ga*MjAwNjIwMjgyMS4xNzEwODMyNTk2*_ga_CW55HF8NVT*MTcxMDgzMjU5NS4xLjAuMTcxMDgzMjU5NS4wLjAuMA..#register_for_remote_notifications"
    // Either at startup, or at the desired point in your application flow, register your app for remote notifications. Call registerForRemoteNotifications as shown:
    fun registerNotification(
        application: UIApplication,
        useBadge: Boolean,
        useSound: Boolean,
        useAlert: Boolean,
        delegate: UNUserNotificationCenterDelegateProtocol
    ) {
        UNUserNotificationCenter.currentNotificationCenter().delegate = delegate

        var types = 0u
        if (useBadge) {
            types = types.or(NotificationType.Badge.value)
        }
        if (useSound) {
            types = types.or(NotificationType.Sound.value)
        }
        if (useAlert) {
            types = types.or(NotificationType.Alert.value)
        }

        UNUserNotificationCenter.currentNotificationCenter()
            .requestAuthorizationWithOptions(
                options = types.toULong(),
                completionHandler = { _, error ->
                    if (error == null) {
                        application.registerForRemoteNotifications()
                    }
                })
    }


    @OptIn(ExperimentalForeignApi::class)
    fun setApsToken(deviceToken: NSData?) {
        FIRMessaging.messaging().setAPNSToken(deviceToken)
    }

    fun onReceived(response: UNNotificationResponse) {
        KMMNotification.processReceived(response)
    }
}
