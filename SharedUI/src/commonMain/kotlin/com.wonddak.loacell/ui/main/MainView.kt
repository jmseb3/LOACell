package com.wonddak.loacell.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.SetTwiceClose
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.common.FABInfo
import com.wonddak.loacell.ui.common.OpenableFabMenu
import com.wonddak.loacell.ui.modal.sheet.RoomSheet
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import kotlinx.coroutines.launch
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.room_enter
import loacell.sharedui.generated.resources.room_make

@Composable
fun MainView(
	navController : NavHostController,
	authViewModel : AuthViewModel,
	raidViewModel : RaidViewModel,
) {
	val roomList by raidViewModel.roomList.collectAsState()

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
			raidViewModel.startObserveRoom(authViewModel.user!!.uid)
		}
		//별개로 raidData는 메인에 오면 계속 탐색할 필요가 없다.
		raidViewModel.stopObserveRaidInfo()
	}
	val fabStatus = rememberModalStatus()
	SetTwiceClose()
	SetBackAction(fabStatus.status) {
		fabStatus.hide()
	}
	val scope = rememberCoroutineScope()
	val roomAddSheet = rememberModalStatus()
	Scaffold(
		topBar = {
			LoaCellTopAppBar(
				"LoaCell",
				actionContent = {
					IconButton(
						onClick = {
							navController.navigate(Const.NAV_SETTING) {
								launchSingleTop = true
							}
						},
					) {
						Icon(Icons.Filled.Settings, contentDescription = "설정")
					}
				}
			)
		},
		floatingActionButton = {
			OpenableFabMenu(
				fabStatus,
				arrayListOf(
					FABInfo.Label(Res.drawable.room_enter, "입장") {
						navController.navigate(Const.NAV_ROOM_ENTER_MAIN) {
							launchSingleTop = true
						}
					}
				).also { list ->
					authViewModel.user?.let {
						if (!it.isAnonymous) {
							list.add(FABInfo.Label(Res.drawable.room_make, "만들기") {
								roomAddSheet.show()
							})
						}
					}
				}
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier.fillMaxSize().padding(innerPadding)
		) {
			if (roomList.isEmpty()) {
				Box(
					modifier = Modifier.fillMaxSize().padding(24.dp),
					contentAlignment = Alignment.Center,
				) {
					Text(
						text = "참여 중인 방이 없습니다.\n오른쪽 아래의 + 버튼을 눌러 방에 입장해 보세요.",
						textAlign = TextAlign.Center,
					)
				}
			} else {
				LazyColumn(
					modifier = Modifier.fillMaxWidth(),
					contentPadding = PaddingValues(10.dp)
				) {
					items(roomList) { roomInfo ->
						TextButton(
							onClick = {
								scope.launch {
									raidViewModel.setRoomId(roomInfo, authViewModel.user?.uid)
									navController.navigate(Const.NAV_ROOM) {
										launchSingleTop = true
									}
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
		RoomSheet(
			roomAddSheet,
			null
		) { title, description, password ->
			raidViewModel.createRoom(title, description, password, authViewModel.user!!.uid) {
				roomAddSheet.hide()
			}
		}
	}
}

@Composable
private fun RoomInfoRow(
	room : RoomInfo,
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
