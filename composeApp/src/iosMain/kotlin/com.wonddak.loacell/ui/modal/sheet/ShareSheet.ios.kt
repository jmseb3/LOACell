package com.wonddak.loacell.ui.modal.sheet

import com.wonddak.loacell.model.RoomInfo
import platform.UIKit.UIPasteboard

actual fun copyToClipboard(data: String): Boolean {
    UIPasteboard.generalPasteboard().string = data
    return true
}

actual fun shareToKakao(data: RoomInfo) {

}