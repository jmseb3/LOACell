package com.wonddak.loacell.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.viewModel.RaidViewModel
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.person
import loacell.composeapp.generated.resources.room
import loacell.composeapp.generated.resources.room_setting
import org.jetbrains.compose.resources.painterResource


@Composable
fun LoaCellBottomAppBar(
    onAction: (() -> Unit)? = null,
    iconImage: ImageVector? = null,
    content: @Composable () -> Unit,
) {
    BottomAppBar(
        floatingActionButton = {
            onAction?.let { onClick ->
                AnimatedVisibility(true) {
                    SmallFloatingActionButton(
                        content = {
                            Icon(iconImage ?: Icons.Filled.Add, null)
                        },
                        onClick = onClick,
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(2.dp),
                    )
                }
            }
        },
        actions = {
            content()
        }
    )
}

@Composable
fun RaidRoomActions(
    raidViewModel: RaidViewModel,
) {
    Row() {
        with(raidViewModel) {
            IconButton(
                {
                    raidViewModel.changeTabState(RoomState.Raid)
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.room),
                    null,
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                {
                    raidViewModel.changeTabState(RoomState.User)
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.person),
                    null,
                    modifier = Modifier.size(30.dp)
                )
            }
            if (role == RoomInfo.RoomRole.OWNER || role == RoomInfo.RoomRole.MANAGER) {
                IconButton(
                    {
                        raidViewModel.changeTabState(RoomState.Setting)
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.room_setting),
                        null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}