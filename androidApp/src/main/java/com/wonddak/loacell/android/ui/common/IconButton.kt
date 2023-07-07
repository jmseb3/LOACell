package com.wonddak.loacell.android.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.RoomState
import dev.icerock.moko.resources.ImageResource

@Composable
fun MyIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes id: Int,
    size: Dp = 30.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.size(size = size),
            painter = painterResource(id),
            contentDescription = ""
        )
    }
}

@Composable
fun MyIconButton(
    modifier: Modifier = Modifier,
    imageResource: ImageResource,
    enabled: Boolean = true,
    size: Dp = 30.dp,
    onClick: () -> Unit
) = MyIconButton(
    modifier = modifier,
    id = imageResource.drawableResId,
    size = size,
    enabled = enabled,
    onClick = onClick
)

@Composable
fun MyRoomIconButton(
    loaCellViewModel: LoaCellViewModel,
    state: RoomState
) {
    val imageResource = when (state) {
        RoomState.Raid -> SharedRes.images.room
        RoomState.User -> SharedRes.images.person
        RoomState.Setting -> SharedRes.images.room_setting
    }
    MyIconButton(
        id = imageResource.drawableResId,
        enabled = (loaCellViewModel.tabState != state),
        onClick = {
            loaCellViewModel.setTabStatus(state)
        }
    )
}