package com.wonddak.loacell.ui.modal.sheet

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.TextTemplate
import com.wonddak.loacell.AppContext
import com.wonddak.loacell.model.RoomInfo
import io.github.aakira.napier.Napier

actual fun copyToClipboard(data: String): Boolean {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    AppContext.get().startActivity(shareIntent)
    return false
}

actual fun shareToKakao(data: RoomInfo) {
    shareToKakao(AppContext.get(), roomInfo = data)
}

internal fun shareToKakao(context: Context, roomInfo: RoomInfo) {
    val TAG = "KAKAO"
    val uniqueId: String = roomInfo.uniqueId

    val defaultText = TextTemplate(
        text = """LoaCell의 ${roomInfo.title}방으로 초대합니다.""".trimIndent(),
        link = Link(
            androidExecutionParams = mapOf("uniqueId" to uniqueId),
            iosExecutionParams = mapOf("uniqueId" to uniqueId)
        )
    )
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        // 카카오톡으로 카카오톡 공유 가능
        ShareClient.instance.shareDefault(context, defaultText) { sharingResult, error ->
            if (error != null) {
                Napier.e(tag = TAG, throwable = error) { "카카오톡 공유 실패" }
            } else if (sharingResult != null) {
                Napier.d(tag = TAG) { "카카오톡 공유 성공 ${sharingResult.intent}" }
                context.startActivity(sharingResult.intent)

                // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                Napier.w(tag = TAG) { "Warning Msg: ${sharingResult.warningMsg}" }
                Napier.w(tag = TAG) { "Argument Msg: ${sharingResult.argumentMsg}" }
            }
        }
    } else {
        // 카카오톡 미설치: 웹 공유 사용 권장
        // 웹 공유 예시 코드
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultText)

        // CustomTabs으로 웹 브라우저 열기

        // 1. CustomTabsServiceConnection 지원 브라우저 열기
        // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
        } catch (e: UnsupportedOperationException) {
            // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
        }

        // 2. CustomTabsServiceConnection 미지원 브라우저 열기
        // ex) 다음, 네이버 등
        try {
            KakaoCustomTabsClient.open(context, sharerUrl)
        } catch (e: ActivityNotFoundException) {
            // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
        }
    }
}