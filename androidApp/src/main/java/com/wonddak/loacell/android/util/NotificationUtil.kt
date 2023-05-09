package com.wonddak.loacell.android.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat

object NotificationUtil {
    const val channelId = "LoaCell_fs_ch"
    private const val channelName = "LoaCell Update Service"
    private const val channelDescription = "LoaCell앱의 데이터 동기화 알림 서비스 입니다."

    fun makeNotificationChannel(context:Context) {
        // 채널 생성
        val mChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        mChannel.description = channelDescription // 사용자에게 표시되는 설명글

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

    }
}