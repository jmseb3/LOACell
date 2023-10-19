package com.wonddak.loacell.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.database.ext.checkTimeOver
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.common.MyRoomIconButton
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonRaidHelper


@Composable
fun BottomAppBar(
    loaCellViewModel: LoaCellViewModel
) {
    val role = loaCellViewModel.myRole

    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val selectedRoomId= totalRoomInfo.roomId
    val focusUserInfo = totalRoomInfo.userInfo
    val focusRaidInfo  = totalRoomInfo.raidInfo
    val tabState = totalRoomInfo.tabState
    val syncData by loaCellViewModel.syncData.collectAsState()

    loaCellViewModel.apply {
        BottomAppBar(
            floatingActionButton = {
                AnimatedVisibility(!(tabState == RoomState.Setting || showSetting)) {
                    SmallFloatingActionButton(
                        content = {
                            if (focusUserInfo != null || focusRaidInfo != null) {
                                Icon(
                                    painter = painterResource(id = SharedRes.images.delete.drawableResId),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(Icons.Filled.Add, null)
                            }
                        },
                        onClick = {
                            bottomAddAction()
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(2.dp),
                    )
                }
            },
            actions = {
                AnimatedVisibility(selectedRoomId.isEmpty() && !loaCellViewModel.showSetting) {
                    Row() {
                        MyIconButton(
                            imageResource = SharedRes.images.refresh,
                            enabled = !syncData
                        ) {
                            syncStart()
                        }
                    }
                }
                AnimatedVisibility(
                    selectedRoomId.isNotEmpty() && (focusUserInfo == null) && (focusRaidInfo == null),
                ) {
                    Row() {
                        MyRoomIconButton(
                            loaCellViewModel = loaCellViewModel,
                            state = RoomState.Raid
                        )
                        MyRoomIconButton(
                            loaCellViewModel = loaCellViewModel,
                            state = RoomState.User
                        )
                        if (role == RoomRole.OWNER || role == RoomRole.MANAGER) {
                            MyRoomIconButton(
                                loaCellViewModel = loaCellViewModel,
                                state = RoomState.Setting
                            )
                        }
                    }

                }
                AnimatedVisibility(
                    selectedRoomId.isNotEmpty() && focusUserInfo != null
                ) {
                    focusUserInfo?.let {
                        Row() {
                            MyIconButton(
                                imageResource = SharedRes.images.change_person
                            ) {
                                showDialog(DialogStatus.CHARACTER_EDIT)
                            }
                            MyIconButton(
                                imageResource = SharedRes.images.refresh,
                                enabled = it.checkTimeOver()
                            ) {
                                updateCharacter(selectedRoomId,it)
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    selectedRoomId.isNotEmpty() && focusRaidInfo != null
                ) {
                    Row() {
                        focusRaidInfo?.let { info ->
                            MyIconButton(
                                imageResource = SharedRes.images.room_setting
                            ) {
                                showDialog(DialogStatus.RAID_EDIT)
                            }
                            val icon = if (info.isFinish) {
                                SharedRes.images.task_finish_done
                            } else {
                                SharedRes.images.task_finish_not
                            }
                            MyIconButton(
                                imageResource = icon
                            ) {
                                CommonRaidHelper.updateFinish(
                                    info.roomId,
                                    info.raidId,
                                    !info.isFinish
                                )
                            }
                        }
                    }
                }
            },
        )
    }
}
