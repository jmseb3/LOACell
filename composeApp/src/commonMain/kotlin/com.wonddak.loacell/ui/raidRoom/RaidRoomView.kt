package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.main.LoaCellBottomAppBar
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.main.RaidRoomActions
import com.wonddak.loacell.ui.modal.sheet.AddRaidSheet
import com.wonddak.loacell.ui.modal.sheet.AddUserSheet
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
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
    val showRaidAddSheet = rememberModalStatus()
    val showUserAddSheet = rememberModalStatus()
    val action: (() -> Unit)? = when (raidViewModel.tabState) {
        RoomState.Raid -> {
            {
                showRaidAddSheet.show()
            }
        }

        RoomState.User -> {
            {
                showUserAddSheet.show()
            }
        }

        else -> {
            null
        }
    }
    val roomInfo = raidViewModel.roomInfo

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
                RaidRoomActions(raidViewModel)
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = modifier.fillMaxSize()
                    .padding(innerPadding)
            ) {
                with(raidViewModel) {
                    roomInfo?.let {
                        TitleView(it, role)
                    }
                    if (tabState == RoomState.Raid) {
                        RaidListView(raidList = raidList) {
                            navController.navigate(Const.NAV_RAID_DETAIL_MAIN + it.raidId)
                        }
                    } else if (tabState == RoomState.User) {
                        UserListView(userList = userList) {
                            navController.navigate(Const.NAV_USER_DETAIL_MAIN + it.name)
                        }
                    }
                }
            }
        }
    }
    AddUserSheet(
        showUserAddSheet,
        Modifier,
        roomInfo!!
    )
    AddRaidSheet(
        showRaidAddSheet,
    ) {

    }
}

@Composable
private fun TitleView(
    roomInfo: RoomInfo,
    role: RoomInfo.RoomRole,
) {
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
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(painterResource(Res.drawable.room_exit), null)
                }
            }
        }
    }
}
