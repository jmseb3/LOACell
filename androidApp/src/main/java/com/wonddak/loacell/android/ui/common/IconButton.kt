package com.wonddak.loacell.android.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.ImageResource

@Composable
fun MyIconButton(
    @DrawableRes id: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    )
    {
        Icon(
            modifier = Modifier.size(size = 30.dp),
            painter = painterResource(id),
            contentDescription = ""
        )
    }
}

@Composable
fun MyIconButton(
    imageResource: ImageResource,
    onClick: () -> Unit
) {
    MyIconButton(
        id = imageResource.drawableResId,
        onClick = onClick
    )
}