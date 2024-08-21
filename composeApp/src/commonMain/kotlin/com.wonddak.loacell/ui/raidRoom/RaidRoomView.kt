package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.model.RoomInfo
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
    roomId: String?,
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
    raidViewModel: RaidViewModel,
) {
    LaunchedEffect(roomId) {
        raidViewModel.startObserveRaidInfoList(roomId)
    }
    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.White)
    ) {
        storeViewModel.findRoomInfo(roomId)?.let { roomInfo ->
            TitleView(roomInfo, authViewModel.user?.uid) {}
        }
        LazyColumn {
            items(raidViewModel.raidList) {
                Text(it.toString())
            }
        }
    }
}

@Composable
private fun TitleView(
    roomInfo: RoomInfo,
    uid: String?,
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

        when (roomInfo.getRole(uid)) {
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
