package com.wonddak.loacell.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wonddak.loacell.BlockBackButton
import com.wonddak.loacell.Const
import com.wonddak.loacell.Const.navigationToRoom
import com.wonddak.loacell.auth.signOut
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel

@Composable
fun MainView(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
    raidViewModel: RaidViewModel,
) {
    LaunchedEffect(authViewModel.user) {
        if (authViewModel.user == null) {
            navController.navigate(Const.NAV_LOGIN) {
                popUpTo(Const.NAV_MAIN) {
                    inclusive = true
                }
            }
        } else {
            storeViewModel.startObserveRoom(authViewModel.user!!.uid)
        }
        raidViewModel.stopObserveRaidInfo()
    }
    BlockBackButton()
    Column(
        modifier = modifier
    ) {
        TextButton(
            onClick = {
                authViewModel.loginHelper.signOut()
            }
        ) {
            Text("Logout")
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(storeViewModel.roomList) { roomInfo ->
                TextButton(
                    onClick = {
                        navController.navigate(roomInfo.navigationToRoom())
                    },
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10))
                        .background(Color.Gray)
                        .padding(5.dp)
                ) {
                    RoomInfoRow(roomInfo)
                }
            }
        }
    }
}

@Composable
fun RoomInfoRow(
    room: RoomInfo,
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = room.title,
            fontSize = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = room.description,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}