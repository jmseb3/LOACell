package com.wonddak.loacell.ui.modal.sheet

import com.wonddak.loacell.model.RoomInfo
import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

private fun copyToClipboard(data: String): Boolean {
    UIPasteboard.generalPasteboard().string = data
    return true
}

private fun shareToKakao(data: RoomInfo) {

}
@Composable actual fun rememberCopyToClipboard(): (String) -> Boolean = { copyToClipboard(it) }
@Composable actual fun rememberShareToKakao(): (RoomInfo) -> Unit = { shareToKakao(it) }
