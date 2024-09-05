package com.wonddak.loacell.ui.raidRoom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.ui.main.LoaCellBottomAppBar
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.main.RaidRoomActions
import com.wonddak.loacell.ui.modal.dialog.RoomExitDialog
import com.wonddak.loacell.ui.modal.sheet.AddUserSheet
import com.wonddak.loacell.ui.modal.sheet.ShareSheet
import com.wonddak.loacell.ui.raidRoom.raid.RaidListView
import com.wonddak.loacell.ui.raidRoom.setting.SettingRoomView
import com.wonddak.loacell.ui.raidRoom.user.UserListView
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.room_exit
import org.jetbrains.compose.resources.painterResource

@Composable
fun RaidRoomView(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
    raidViewModel: RaidViewModel,
) {
    val pagerState = rememberPagerState(pageCount = {
        if (raidViewModel.role == RoomInfo.RoomRole.OWNER) 3 else 2
    })
    val showUserAddSheet = rememberModalStatus()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val action: (() -> Unit)? = when (pagerState.currentPage) {
        RoomState.Raid.index -> {
            {
                navController.navigate(Const.NAV_RAID_ADD)
            }
        }

        RoomState.User.index -> {
            {
                showUserAddSheet.show()
            }
        }

        else -> {
            null
        }
    }
    val roomInfo = storeViewModel.roomList.find { it.uniqueId == raidViewModel.roomId }
    val scope = rememberCoroutineScope()

    fun showSnackBarMsg(msg: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg, actionLabel = "확인")
        }
    }

    Scaffold(
        topBar = {
            LoaCellTopAppBar(
                roomInfo?.title ?: "",
            ) {
                navController.popBackStack()
            }
        },
        bottomBar = {
            LoaCellBottomAppBar(
                onAction = action
            ) {
                RaidRoomActions(raidViewModel.role) {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            with(raidViewModel) {
                AnimatedVisibility(pagerState.currentPage == 0 || pagerState.currentPage == 1) {
                    roomInfo?.let {
                        TitleView(
                            it,
                            authViewModel.user!!.uid,
                            role,
                            ::showSnackBarMsg,
                            navController::popBackStack
                        )
                    }
                }
                HorizontalPager(
                    pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        RoomState.Raid.index -> {
                            RaidListView(
                                raidViewModel
                            ) {
                                navController.navigate(Const.NAV_RAID_DETAIL_MAIN + it.raidId)
                            }
                        }

                        RoomState.User.index -> {
                            UserListView(userList = userList) {
                                navController.navigate(Const.NAV_USER_DETAIL_MAIN + it.name)
                            }
                        }

                        else -> {
                            roomInfo?.let {
                                SettingRoomView(
                                    raidViewModel,
                                    authViewModel,
                                    it,
                                    backToHome = {
                                        navController.popBackStack()
                                    }
                                ) {
                                    scope.launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(it, actionLabel = "확인")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    roomInfo?.let {
        AddUserSheet(
            showUserAddSheet,
            Modifier,
            it
        )
    }
}

@Composable
private fun TitleView(
    roomInfo: RoomInfo,
    uid: String,
    role: RoomInfo.RoomRole,
    showSnackBar: (String) -> Unit,
    onBack: () -> Unit,
) {
    val shareStatus = rememberModalStatus()
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = roomInfo.description,
                modifier = Modifier
            )
            Text(
                text = roomInfo.uniqueId,
                modifier = Modifier.noRippleClickable {
                    shareStatus.show()
                },
            )
            HorizontalDivider()
        }

        when (role) {
            RoomInfo.RoomRole.OWNER -> {

            }

            RoomInfo.RoomRole.NONE -> {

            }

            else -> {
                val exitRoomStatus = rememberModalStatus()
                IconButton(
                    onClick = {
                        exitRoomStatus.show()
                    }
                ) {
                    Icon(painterResource(Res.drawable.room_exit), null)
                }
                RoomExitDialog(
                    exitRoomStatus
                ) {
                    CommonRoomHelper.exitRoom(
                        roomInfo.uniqueId,
                        uid,
                        role,
                        {
                            exitRoomStatus.hide()
                            onBack()
                        },
                        {
                            exitRoomStatus.hide()
                        }
                    )
                }
            }
        }
    }
    ShareSheet(
        shareStatus,
        roomInfo,
        showSnackBar
    )
}
