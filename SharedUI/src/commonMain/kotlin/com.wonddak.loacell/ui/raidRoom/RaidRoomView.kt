package com.wonddak.loacell.ui.raidRoom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.rememberModalStatus
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.room_exit
import org.jetbrains.compose.resources.painterResource

@Composable
fun RaidRoomView(
    modifier: Modifier,
    authViewModel: AuthViewModel,
    raidViewModel: RaidViewModel,
    navigateRaidAdd: () -> Unit,
    navigateRaidDetail: (raidId: String) -> Unit,
    navigateUserDetail: (userName: String) -> Unit,
    onBack: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = raidViewModel.lastTabIndex,
        pageCount = {
            if (raidViewModel.role == RoomInfo.RoomRole.OWNER) 3 else 2
        }
    )
    LaunchedEffect(pagerState.currentPage) {
        raidViewModel.lastTabIndex = pagerState.currentPage
    }
    val showUserAddSheet = rememberModalStatus()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    val action: (() -> Unit)? = when (pagerState.currentPage) {
        RoomState.Raid.index -> {
            {
                navigateRaidAdd()
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
    val selectedRoomInfo by raidViewModel.selectedRoomInfo.collectAsState(null)

    LaunchedEffect(selectedRoomInfo) {
        if (raidViewModel.role == RoomInfo.RoomRole.NONE) {
            selectedRoomInfo?.let {
                raidViewModel.refreshRole(authViewModel.user?.uid, it)
            }
        }

    }
    val scope = rememberCoroutineScope()

    fun showSnackBarMsg(msg: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg, actionLabel = "확인")
        }
    }

    selectedRoomInfo?.let { roomInfo ->
        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    roomInfo.title,
                    onBack = onBack
                )
            },
            bottomBar = {
                LoaCellBottomAppBar(
                    onAction = action
                ) {
                    RaidRoomActions(raidViewModel.role, pagerState.currentPage) {
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
                        TitleView(
                            roomInfo,
                            authViewModel.user!!.uid,
                            role,
							raidViewModel,
                            ::showSnackBarMsg,
                            onBack
                        )
                    }
                    HorizontalPager(
                        pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        when (page) {
                            RoomState.Raid.index -> {
                                RaidListView(
                                    raidViewModel = raidViewModel,
                                    navigation = { navigateRaidDetail(it.raidId) }
                                )
                            }

                            RoomState.User.index -> {
                                UserListView(userList = userList) {
                                    navigateUserDetail(it.name)
                                }
                            }

                            else -> {
                                SettingRoomView(
                                    raidViewModel,
                                    authViewModel,
                                    roomInfo,
                                    onBack
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
        AddUserSheet(
            showUserAddSheet,
            Modifier,
            roomInfo,
            raidViewModel,
        )
    } ?: Column {
        var show by remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            delay(1000)
            show = true
        }
        if (show) {
            TextButton(
                onClick = onBack
            ) {
                Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
            }
        }
    }
}

@Composable
private fun TitleView(
    roomInfo: RoomInfo,
    uid: String,
    role: RoomInfo.RoomRole,
	raidViewModel: RaidViewModel,
    showSnackBar: (String) -> Unit,
    onBack: () -> Unit,
) {
    val shareStatus = rememberModalStatus()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp),
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
            }

            when (role) {
                RoomInfo.RoomRole.OWNER -> {

                }

                RoomInfo.RoomRole.NONE -> {

                }

                else -> {
                    val exitRoomStatus = rememberModalStatus()
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        onClick = {
                            exitRoomStatus.show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.room_exit),
                            contentDescription = "방 나가기",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    RoomExitDialog(
                        exitRoomStatus
                    ) {
                        raidViewModel.exitRoom(
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
        HorizontalDivider()
    }

    ShareSheet(
        shareStatus,
        roomInfo,
        showSnackBar
    )
}
