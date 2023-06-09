package com.wonddak.loacell.android.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.store.CommonRaidHelper


@Composable
fun BottomAppBar(
    loaCellViewModel: LoaCellViewModel
) {
    val selectedRoomId by loaCellViewModel.roomId.collectAsState()
    val focusUserInfo by loaCellViewModel.userInfo.collectAsState()
    val focusRaidInfo by loaCellViewModel.raidInfo.collectAsState()

    loaCellViewModel.apply {
        BottomAppBar(
            floatingActionButton = {
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
            },
            actions = {
                val showLevel1 =
                    selectedRoomId.isNotEmpty() && (focusUserInfo == null) && (focusRaidInfo == null)
                AnimatedVisibility(
                    showLevel1,
                ) {
                    Row() {
                        MyIconButton(
                            imageResource = SharedRes.images.room,
                            enabled = tabState != 0
                        ) { loaCellViewModel.setTabStatus(0) }
                        MyIconButton(
                            imageResource = SharedRes.images.person,
                            enabled = tabState != 1
                        ) { loaCellViewModel.setTabStatus(1) }
                    }

                }
                AnimatedVisibility(
                    focusUserInfo != null
                ) {
                    focusUserInfo?.let {
                        Row() {
                            IconButton(onClick = { clearFocusItem() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                            MyIconButton(
                                imageResource = SharedRes.images.change_person
                            ) {
                                showCharacterEdit = true
                            }
                            MyIconButton(
                                imageResource = SharedRes.images.refresh,
                                enabled = it.checkTimeOver(System.currentTimeMillis())
                            ) {
                                showLoading = true
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    focusRaidInfo != null
                ) {
                    Row() {
                        IconButton(onClick = { clearFocusItem() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null)
                        }
                        focusRaidInfo?.let {info ->
                            MyIconButton(
                                imageResource = SharedRes.images.room_setting
                            ) {
                                loaCellViewModel.showRaidEdit = true
                            }
                            val icon = if (info.isFinish) {
                                SharedRes.images.task_finish_done
                            } else {
                                SharedRes.images.task_finish_not
                            }
                            MyIconButton(
                                imageResource = icon
                            ) {
                                CommonRaidHelper.updateFinish(info.roomId,info.raidId,!info.isFinish)
                            }
                        }
                    }
                }
            },
        )
    }
}
