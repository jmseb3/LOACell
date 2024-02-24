package com.wonddak.loacell.android

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit = {},
    crossinline onLongClick: () -> Unit = {}
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
inline fun StringDesc.toText(): String {
    return this.toString(context = LocalContext.current)
}

@Composable
inline fun ImageResource.toPainter(): Painter {
    return painterResource(id = this.drawableResId)
}