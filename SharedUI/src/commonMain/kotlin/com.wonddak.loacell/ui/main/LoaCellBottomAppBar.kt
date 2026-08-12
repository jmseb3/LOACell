package com.wonddak.loacell.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.person
import loacell.sharedui.generated.resources.room
import loacell.sharedui.generated.resources.room_setting
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
                            Icon(iconImage ?: Icons.Filled.Add, contentDescription = "추가")
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
    role: RoomInfo.RoomRole,
    selectedPage: Int,
    changePage: (Int) -> Unit
) {
    Row() {
        RoomNavigationAction(
            icon = Res.drawable.room,
            label = "레이드",
            contentDescription = "레이드 목록",
            selected = selectedPage == RoomState.Raid.index,
            onClick = { changePage(RoomState.Raid.index) },
        )
        RoomNavigationAction(
            icon = Res.drawable.person,
            label = "참여자",
            contentDescription = "참여자 목록",
            selected = selectedPage == RoomState.User.index,
            onClick = { changePage(RoomState.User.index) },
        )
        if (role == RoomInfo.RoomRole.OWNER || role == RoomInfo.RoomRole.MANAGER) {
            RoomNavigationAction(
                icon = Res.drawable.room_setting,
                label = "방 설정",
                contentDescription = "방 설정",
                selected = selectedPage == RoomState.Setting.index,
                onClick = { changePage(RoomState.Setting.index) },
            )
        }
    }
}

@Composable
private fun RoomNavigationAction(
    icon: org.jetbrains.compose.resources.DrawableResource,
    label: String,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier.semantics {
            this.selected = selected
            role = Role.Tab
        },
        onClick = onClick,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
