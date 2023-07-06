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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.bottomSheet.EditRoomSheet
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.common.SectionCardView
import com.wonddak.loacell.android.ui.dialog.ChangeOwnerDialog
import com.wonddak.loacell.android.ui.dialog.ConfirmDialog
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.checkNotExistUid
import com.wonddak.loacell.ext.getAllUidList
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.sharedapi.firebase.model.FBDataItem

@Composable
fun SettingRoomView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    var fetch by remember {
        mutableStateOf(false)
    }
    val result = loaCellViewModel.tempOfFBData
    val user by loaCellViewModel.user.collectAsState()
    val roomInfo by loaCellViewModel.roomInfo.collectAsState()
    val raidList by loaCellViewModel.raidInfoList.collectAsState()
    val userList by loaCellViewModel.userInfoList.collectAsState()

    var showExitAlert by remember {
        mutableStateOf(false)
    }

    var showChangeAlert by remember() {
        mutableStateOf(false)
    }

    roomInfo?.let { info ->
        //유저 정보랑 owner랑 같은 경우 바로 가져오기 가능
        user?.let { userInfo ->
            if (info.getAllUidList().size == 1 && info.owner == userInfo.uid) {
                loaCellViewModel.tempOfFBData = listOf(
                    FBDataItem(
                        userInfo.uid,
                        userInfo.displayName,
                        userInfo.photoUrl.toString()
                    )
                )
            }
        }
        //이전 값이랑 같으면 갱신 pass
        if (info.getAllUidList() == result.map { it.uid }) {
            fetch = true
        }
        LaunchedEffect(true) {
            info.checkNotExistUid { data ->
                fetch = true
                loaCellViewModel.tempOfFBData = data
            }
        }
        if (showExitAlert) {
            ConfirmDialog(
                title = "나가기",
                bodyText = "정말 해당 방에서 나갈까요?\n 삭제된 데이터는 복구가 불가능합니다.",
                confirmButtonText = "나가기",
                confirm = {
                    CommonRoomHelper.deleteRoom(
                        info.uniqueId,
                        successAction = {
                            loaCellViewModel.hideRoomInfo()
                            db.roomInfoQueriesHelper.deleteRoomInfo(info.uniqueId)
                            showExitAlert = false
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

        if (showChangeAlert) {
            ChangeOwnerDialog(
                owner = info.owner,
                fbDataList = result,
                confirm = { uid ->
                    CommonRoomHelper.changeOwner(
                        roomId = info.uniqueId,
                        preOwner = info.owner,
                        newOwnerUid = uid,
                        commonAction = {
                            showChangeAlert = false
                        },
                        successAction = {
                            loaCellViewModel.hideRoomInfo()
                        },
                        failAction =  {
                            loaCellViewModel.showSnackBar("변경에 실패했습니다.${it.errorMsg}")
                        }
                    )
                },
                dismiss =  {
                    showChangeAlert = false
                }
            )
        }
        Column {
            SettingRoomInfo(loaCellViewModel, info)
            Divider()
            if (fetch) {
                SectionCardView("방 관리") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val otherMemberList = info.enterUser + info.editableUser
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
                        TextButton(
                            onClick = {
                                showChangeAlert = true
                            },
                            enabled = otherMemberList.isNotEmpty()
                        ) {
                            Text(text = "소유자 위임")
                        }
                    }
                }
            }
            Divider()
            UserUidList(fetch, info, result)
        }
    }
}

@Composable
fun SettingRoomInfo(
    loaCellViewModel: LoaCellViewModel,
    roomInfo: RoomInfo
) {
    var showPassword by remember {
        mutableStateOf(false)
    }
    SectionCardView(
        title = "방 정보",
        icon = SharedRes.images.edit.drawableResId,
        iconAction = {
            loaCellViewModel.showRoomEdit = true
        }
    ) {
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
    if (loaCellViewModel.showRoomEdit) {
        EditRoomSheet(
            getTitle = roomInfo.title,
            getDescription = roomInfo.description,
            getPassword = roomInfo.enterPassword,
            onDismissRequest = { loaCellViewModel.showRoomEdit = false },
            editAction = { title, description, password ->
                CommonRoomHelper.updateRoom(
                    roomInfo.uniqueId, title, description, password,
                    successAction = {
                        loaCellViewModel.showRoomEdit = false
                        showPassword = false
                    },
                    failAction = {
                        loaCellViewModel.showRoomEdit = false
                        loaCellViewModel.showSnackBar("변경에 실패했습니다(${it.errorMsg}")
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserUidList(
    fetch: Boolean,
    roomInfo: RoomInfo,
    result: List<FBDataItem>
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
                        items(filter) { id ->
                            UserUidItem(
                                roomId = roomInfo.uniqueId,
                                name = name,
                                uid = id,
                                fbData = result
                            )
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
) {
    val find = fbData.find { it.uid == uid }
    var showConfirm by remember {
        mutableStateOf(false)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (find != null) {
            Text(text = find.getName())
        } else {
            Text(text = uid)
        }
        Spacer(modifier = Modifier.weight(1f))
        if ((name != RoomRole.OWNER.toName)) {
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


}
