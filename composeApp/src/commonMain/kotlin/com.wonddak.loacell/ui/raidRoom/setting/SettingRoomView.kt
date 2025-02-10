package com.wonddak.loacell.ui.raidRoom.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.network.firebase.model.FBDataItem
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.Error
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.ui.common.SectionCardView
import com.wonddak.loacell.ui.modal.dialog.ConfirmDialog
import com.wonddak.loacell.ui.modal.sheet.RoomSheet
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingRoomView(
    raidViewModel: RaidViewModel,
    authViewModel: AuthViewModel,
    roomInfo: RoomInfo,
    backToHome: () -> Unit,
    showSnackBar: (String) -> Unit,
) {
    SetBackAction(true) {
        backToHome()
    }
    val result by raidViewModel.tempOfFBData.collectAsState()
    val user = authViewModel.user
    val userList = raidViewModel.userList
    val raidList = raidViewModel.raidList
    LaunchedEffect(true) {
        //유저 정보랑 owner랑 같은 경우 바로 가져오기 가능
        user?.let { userInfo ->
            if (roomInfo.getAllUidList().size == 1 && roomInfo.owner == userInfo.uid) {
                raidViewModel.initFBData(
                    listOf(
                        FBDataItem(
                            userInfo.uid,
                            userInfo.displayName,
                            userInfo.photoUrl
                        )
                    )
                )
            }
        }
        //이전 값이랑 같으면 갱신 pass
        if (roomInfo.getAllUidList() == result.map { it.uid }) {
            raidViewModel.fetch = true
        }
        if (!raidViewModel.fetch) {
            raidViewModel.fetchFBData(roomInfo)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SettingRoomInfo(roomInfo, raidList, userList, backToHome, showSnackBar)
        HorizontalDivider()
        UserUidList(
            raidViewModel.fetch,
            roomInfo,
            result,
            ownerChangeSuccess = {
                backToHome()
            }
        ) { err ->
            showSnackBar("변경에 실패했습니다.${err.errorMsg}")
        }
    }
}

@Composable
fun SettingRoomInfo(
    roomInfo: RoomInfo,
    raidList: List<RaidInfo>,
    userList: List<UserInfo>,
    backToHome: () -> Unit,
    showSnackBar: (String) -> Unit,
) {
    var showPassword by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val showExitAlert = rememberModalStatus()
    val showRoomEdit = rememberModalStatus()
    SectionCardView(
        title = "방 정보",
        icon = Res.drawable.edit,
        iconAction = {
            showRoomEdit.show()
        }
    ) {
        val otherMemberList = roomInfo.enterUser + roomInfo.editableUser
        TextButton(
            onClick = {
                if (raidList.isEmpty() && userList.isEmpty()) {
                    showExitAlert.show()
                } else {
                    showSnackBar("레이드 정보/유저 정보를 모두 삭제해주세요.")
                }
            },
            enabled = otherMemberList.isEmpty()
        ) {
            Text(text = "나가기")
        }
        HorizontalDivider()
        listOf(
            "제목",
            roomInfo.title,
            "설명",
            roomInfo.description
        ).forEach {
            Text(it)
        }
        if (roomInfo.enterPassword.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column() {
                    Text(text = "비밀번호")
                    Text(text = if (!showPassword) "*".repeat(10) else roomInfo.enterPassword)
                }
                IconButton({ showPassword = !showPassword }) {
                    val res =
                        if (showPassword) Res.drawable.visible_off else Res.drawable.visible_on
                    Icon(painter = painterResource(res), null, Modifier.size(22.dp))
                }
            }
        }
    }
    ConfirmDialog(
        showExitAlert,
        title = "나가기",
        bodyText = "정말 해당 방에서 나갈까요?\n 삭제된 데이터는 복구가 불가능합니다.",
        confirmButtonText = "나가기",
        confirm = {
            CommonRoomHelper.deleteRoom(
                roomInfo.uniqueId,
                successAction = {
                    scope.launch {
                        showExitAlert.hide()
                        backToHome()
                    }
                },
                failAction = {
                    showExitAlert.hide()
                    showSnackBar("나가기에 실패했습니다. 관리자에게 문의하세요${it.errorMsg}")
                }
            )
        }
    )
    RoomSheet(
        showRoomEdit,
        roomInfo
    ) { title, description, password ->
        CommonRoomHelper.updateRoom(
            roomInfo.uniqueId,
            title,
            description,
            password,
            { showRoomEdit.hide() },
            { showRoomEdit.hide() })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserUidList(
    fetch: Boolean,
    roomInfo: RoomInfo,
    result: List<FBDataItem>,
    ownerChangeSuccess: () -> Unit,
    ownerChangeFail: (error: Error) -> Unit,
) {
    if (!fetch) {
        LoadingView("유저 정보를 가져옵니다.", Color.White.copy(0.3f))
    } else {
        SectionCardView("사용자 정보") {
            val group = mapOf(
                RoomInfo.RoomRole.OWNER.toName to listOf(roomInfo.owner),
                RoomInfo.RoomRole.MANAGER.toName to roomInfo.editableUser,
                RoomInfo.RoomRole.USER.toName to roomInfo.enterUser
            )
            LazyColumn {
                group.forEach { (name, data) ->
                    val filter = data.filter { it.isNotEmpty() }
                    if (filter.isNotEmpty()) {
                        stickyHeader {
                            Text(
                                text = name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Gray),
                                fontSize = 16.sp
                            )
                        }
                        items(filter) { uid ->
                            UserUidItem(
                                roomId = roomInfo.uniqueId,
                                name = name,
                                uid = uid,
                                fbData = result
                            ) {
                                CommonRoomHelper.changeOwner(
                                    roomId = roomInfo.uniqueId,
                                    preOwner = roomInfo.owner,
                                    newOwnerUid = uid,
                                    commonAction = {},
                                    successAction = ownerChangeSuccess,
                                    failAction = ownerChangeFail
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserUidItem(
    roomId: String,
    name: String,
    uid: String,
    fbData: List<FBDataItem>,
    changeOwner: () -> Unit,
) {
    val find = fbData.find { it.uid == uid }
    val showConfirm = rememberModalStatus()
    val showChangeAlert = rememberModalStatus()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = find?.getName() ?: uid)
        Spacer(modifier = Modifier.weight(1f))
        if ((name != RoomInfo.RoomRole.OWNER.toName)) {
            TextButton(
                onClick = {
                    showChangeAlert.show()
                },
            ) {
                Text(text = "위임")
            }
            IconButton(
                {
                    showConfirm.show()
                }
            ) {
                Icon(painter = painterResource(Res.drawable.room_exit), null, Modifier.size(22.dp))
            }
        }
    }
    ConfirmDialog(
        showConfirm,
        title = "내보내기",
        bodyText = "해당 유저를 방에서 정말 내보내시겠습니까?",
        confirm = {
            if (name == RoomInfo.RoomRole.MANAGER.toName) {
                CommonRoomHelper.exitEditableUserFromRoom(roomId, listOf(uid)) {
                    showConfirm.hide()
                }
            }
            if (name == RoomInfo.RoomRole.USER.toName) {
                CommonRoomHelper.exitEnterUserFromRoom(roomId, listOf(uid)) {
                    showConfirm.hide()
                }
            }
        }
    )

    ConfirmDialog(
        showChangeAlert,
        title = "소유자 위임",
        bodyText = "해당 유저에게 소유자권한을 위임 하시겠습니까?",
        confirm = {
            changeOwner()
            showChangeAlert.hide()
        },
    )
}
