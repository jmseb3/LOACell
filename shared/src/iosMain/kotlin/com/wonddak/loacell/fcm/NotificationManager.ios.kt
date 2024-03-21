package com.wonddak.loacell.fcm

import cocoapods.FirebaseMessaging.FIRMessaging
import cocoapods.FirebaseMessaging.FIRMessagingDelegateProtocol
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class NotificationManager : NSObject(), FIRMessagingDelegateProtocol {

    private val fcm: FIRMessaging
        get() = FIRMessaging.messaging()

    init {
        fcm.delegate = this

        fcm.tokenWithCompletion { token, error ->
            if (error == null) {
                this.token = token
            }
        }
    }

    actual var token: String? = null

    override fun messaging(messaging: FIRMessaging, didReceiveRegistrationToken: String?) {
        super.messaging(messaging, didReceiveRegistrationToken)
        val dataDict: Map<Any?, *> = mapOf("token" to (didReceiveRegistrationToken ?: ""))
        NSNotificationCenter.defaultCenter().postNotificationName(
            aName = "FCMToken",
            `object` = null,
            userInfo = dataDict
        )
        KMMNotification.notifyNewToken(didReceiveRegistrationToken)
    }

}