package com.wonddak.loacell

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit = {},
    crossinline onLongClick: () -> Unit = {},
): Modifier = composed {
    this.clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
    }
    this.combinedClickable(
        onClick = { onClick() },
        onLongClick = { onLongClick() },
    )
}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit = {},
): Modifier = composed {
    this.clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun BlockBackButton() {
    SetBackAction(true) {}
}

@Composable
expect fun SetBackAction(enabled: Boolean, action: () -> Unit)

@Composable
fun rememberModalStatus() = remember {
    val modalStatus = ModalStatus()
    modalStatus
}

class ModalStatus() {
    var status by mutableStateOf(false)
        private set

    fun show() {
        status = true
    }

    fun hide() {
        status = false
    }

    fun toggle() {
        status = !status
    }
}