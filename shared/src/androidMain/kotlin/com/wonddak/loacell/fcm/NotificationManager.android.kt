package com.wonddak.loacell.fcm

import com.google.firebase.messaging.FirebaseMessaging

actual class NotificationManager {
    actual var token: String? = null

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {task ->
            if (task.isSuccessful) {
                this.token = task.result
            }
        }
    }
}