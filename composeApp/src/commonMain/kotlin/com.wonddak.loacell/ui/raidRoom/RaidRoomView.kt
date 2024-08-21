package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.noRippleClickable
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
    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.White)
    ) {
        with(raidViewModel) {
            roomInfo?.let {
                TitleView(it, role) {

                }
            }
            if (tabState == RoomState.Raid) {
                RaidListView(raidList = raidList) {
                    navController.navigate(Const.NAV_RAID_DETAIL)
                }
            }
        }
    }
}

@Composable
private fun TitleView(
    roomInfo: RoomInfo,
    role: RoomInfo.RoomRole,
    showDialog: (status: Modal) -> Unit,
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
                    showDialog(Sheet.SHARE_SHEET)
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
