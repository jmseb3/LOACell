package com.wonddak.loacell.android.ui.room.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.common.SectionCardView
import com.wonddak.loacell.android.ui.modal.dialog.ConfirmDialog
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.checkNotExistUid
import com.wonddak.loacell.ext.getAllUidList
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.Error
import com.wonddak.sharedapi.firebase.model.FBDataItem
import kotlinx.coroutines.launch

@Composable
fun SettingRoomView(
    loaCellViewModel: LoaCellViewModel
) {
    var fetch by remember {
        mutableStateOf(false)
    }
    val result = loaCellViewModel.tempOfFBData
    val user = loaCellViewModel.user
    val totalRoomInfo = loaCellViewModel.totalRoomInfo
    val roomInfo = totalRoomInfo.roomInfo
    val raidList = totalRoomInfo.raidInfoList
    val userList = totalRoomInfo.userInfoList

    roomInfo?.let { info ->
        LaunchedEffect(true) {
            //유저 정보랑 owner랑 같은 경우 바로 가져오기 가능
            user?.let { userInfo ->
                if (info.getAllUidList().size == 1 && info.owner == userInfo.uid) {
                    loaCellViewModel.tempOfFBData = listOf(
                        FBDataItem(
                            userInfo.uid,
                            userInfo.displayName,
                            userInfo.photoUrl
                        )
                    )
                }
            }
            //이전 값이랑 같으면 갱신 pass
            if (info.getAllUidList() == result.map { it.uid }) {
                fetch = true
            }
            if(!fetch) {
                info.checkNotExistUid { data ->
                    fetch = true
                    loaCellViewModel.tempOfFBData = data
                }
            }
        }
        Column {
            SettingRoomInfo(loaCellViewModel, info, raidList, userList)
            HorizontalDivider()
            UserUidList(
                fetch,
                info,
                result,
                ownerChangeSuccess = { loaCellViewModel.hideRoomInfo() }) { err ->
                loaCellViewModel.showSnackBar(msg = "변경에 실패했습니다.${err.errorMsg}")
            }
        }
    }
}

@Composable
fun SettingRoomInfo(
    loaCellViewModel: LoaCellViewModel,
    roomInfo: RoomInfo,
    raidList: List<RaidInfo>,
    userList: List<UserInfo>
) {
    var showPassword by remember {
        mutableStateOf(false)
    }

    var showExitAlert by remember {
        mutableStateOf(false)
    }
    SectionCardView(
        title = "방 정보",
        icon = SharedRes.images.edit.drawableResId,
        iconAction = {
            loaCellViewModel.showDialog(Sheet.ROOM_EDIT)
        }
    ) {
        val otherMemberList = roomInfo.enterUser + roomInfo.editableUser
        TextButton(
            onClick = {
                if (raidList.isEmpty() && userList.isEmpty()) {
                    showExitAlert = true
                } else {
                    loaCellViewModel.showSnackBar("레이드 정보/유저 정보를 모두 삭제해주세요.")
                }
            },
            enabled = otherMemberList.isEmpty()
        ) {
            Text(text = "나가기")
        }
        HorizontalDivider()
        Text(text = "제목")
        Text(text = roomInfo.title)
        Text(text = "설명")
        Text(text = roomInfo.description)
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
                MyIconButton(imageResource = if (showPassword) SharedRes.images.visible_off else SharedRes.images.visible_on) {
                    showPassword = !showPassword
                }
            }
        }
    }
    val scope = rememberCoroutineScope()
    if (showExitAlert) {
        ConfirmDialog(
            title = "나가기",
            bodyText = "정말 해당 방에서 나갈까요?\n 삭제된 데이터는 복구가 불가능합니다.",
            confirmButtonText = "나가기",
            confirm = {
                CommonRoomHelper.deleteRoom(
                    roomInfo.uniqueId,
                    successAction = {
                        scope.launch {
                            loaCellViewModel.deleteRoom(roomInfo.uniqueId)
                            showExitAlert = false
                        }
                    },
                    failAction = {
                        loaCellViewModel.showSnackBar("나가기에 실패했습니다. 관리자에게 문의하세요${it.errorMsg}")
                        showExitAlert = false
                    }
                )
            },
            dismiss = {
                showExitAlert = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserUidList(
    fetch: Boolean,
    roomInfo: RoomInfo,
    result: List<FBDataItem>,
    ownerChangeSuccess: () -> Unit,
    ownerChangeFail: (error: Error) -> Unit
) {
    if (!fetch) {
        LoadingView("유저 정보를 가져옵니다.", Color.White.copy(0.3f))
    } else {
        SectionCardView("사용자 정보") {
            val group = mapOf(
                RoomRole.OWNER.toName to listOf(roomInfo.owner),
                RoomRole.MANAGER.toName to roomInfo.editableUser,
                RoomRole.USER.toName to roomInfo.enterUser
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
    changeOwner: () -> Unit
) {
    val find = fbData.find { it.uid == uid }
    var showConfirm by remember {
        mutableStateOf(false)
    }
    var showChangeAlert by remember() {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = find?.getName() ?: uid)
        Spacer(modifier = Modifier.weight(1f))
        if ((name != RoomRole.OWNER.toName)) {
            TextButton(
                onClick = {
                    showChangeAlert = true
                },
            ) {
                Text(text = "위임")
            }
            MyIconButton(imageResource = SharedRes.images.room_exit, size = 22.dp) {
                showConfirm = true
            }
        }
    }
    if (showConfirm) {
        ConfirmDialog(
            title = "내보내기",
            bodyText = "해당 유저를 방에서 정말 내보내시겠습니까?",
            confirm = {
                if (name == RoomRole.MANAGER.toName) {
                    CommonRoomHelper.exitEditableUserFromRoom(roomId, listOf(uid)) {
                        showConfirm = false
                    }
                }
                if (name == RoomRole.USER.toName) {
                    CommonRoomHelper.exitEnterUserFromRoom(roomId, listOf(uid)) {
                        showConfirm = false
                    }
                }
            },
            dismiss = {
                showConfirm = false
            }
        )
    }
    if (showChangeAlert) {
        ConfirmDialog(
            title = "소유자 위임",
            bodyText = "해당 유저에게 소유자권한을 위임 하시겠습니까?",
            confirm = {
                changeOwner()
                showChangeAlert = false
            },
            dismiss = {
                showChangeAlert = false
            }
        )
    }


}
