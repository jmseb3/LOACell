package com.wonddak.loacell.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wonddak.loacell.BlockBackButton
import com.wonddak.loacell.Const
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.auth.signOut
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.room_enter
import loacell.composeapp.generated.resources.room_make

@Composable
fun MainView(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
    raidViewModel: RaidViewModel,
) {
    LaunchedEffect(authViewModel.user) {
        //메인에서 유저 정보에 변동이 생긴 경우
        if (authViewModel.user == null) {
            //로그아웃 된경우
            //다시 로그인으로 보낸다.
            navController.navigate(Const.NAV_LOGIN) {
                popUpTo(Const.NAV_MAIN) {
                    inclusive = true
                    saveState = true
                }
            }
        } else {
            //로그인 된경우
            //다시 로그인으로 보낸다.
            storeViewModel.startObserveRoom(authViewModel.user!!.uid)
        }
        //별개로 raidData는 메인에 오면 계속 탐색할 필요가 없다.
        raidViewModel.stopObserveRaidInfo()
    }
    val fabStatus = rememberModalStatus()
    BlockBackButton()
    SetBackAction(fabStatus.status) {
        fabStatus.hide()
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            LoaCellTopAppBar("LoaCell")
        },
        floatingActionButton = {
            OpenableFabMenu(
                fabStatus,
                listOf(
                    FABInfo.Label(Res.drawable.room_enter, "입장") {

                    },
                    FABInfo.Label(Res.drawable.room_make, "만들기") {

                    }
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
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
                            scope.launch {
                                raidViewModel.roomInfo = roomInfo
                                raidViewModel.startObserveRaidInfoList(authViewModel.user?.uid)
                                navController.navigate(Const.NAV_ROOM)
                            }
                        },
                        Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10))
                            .background(Color.Gray)
                    ) {
                        RoomInfoRow(roomInfo)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomInfoRow(
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