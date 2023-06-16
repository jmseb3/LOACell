package com.wonddak.loacell.android.ui.room.setting

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.checkNotExistUid
import com.wonddak.loacell.ext.getAllUidList
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.sharedapi.FBDataItem

@Composable
fun SettingRoomView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    var fetch by remember {
        mutableStateOf(false)
    }
    val result = loaCellViewModel.tempOfFBData
    val roomInfo by loaCellViewModel.roomInfo.collectAsState()
    roomInfo?.let { info ->
        if (info.getAllUidList() == result.map { it.uid }) {
            fetch = true
        }
        LaunchedEffect(true) {
            info.checkNotExistUid { data ->
                fetch = true
                loaCellViewModel.tempOfFBData = data
            }
        }
        Column {
            SettingRoomInfo(loaCellViewModel,info)
            Divider()
            UserUidList(fetch,info,result)
        }
    }
}

@Composable
private fun SectionCardView(
    title: String? = null,
    @DrawableRes icon: Int = 0,
    iconAction: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box() {
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        fontSize = 20.sp
                    )
                }
                if (icon != 0) {
                    MyIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        id = icon,
                        size = 18.dp
                    ) {
                        iconAction()
                    }
                }
            }
            if (title != null || icon != 0) {
                Divider()
            }
            content()
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
        iconAction =  {
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
                            UserUidItem(uid = id, fbData = result,showButton = (name != RoomRole.OWNER.toName)) {
                                if (name == RoomRole.MANAGER.toName) {
                                    CommonRoomHelper.exitEditableUserFromRoom(roomInfo.uniqueId, listOf(id)) {

                                    }
                                }
                                if (name == RoomRole.USER.toName) {
                                    CommonRoomHelper.exitEnterUserFromRoom(roomInfo.uniqueId, listOf(id)) {

                                    }
                                }
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
    uid: String,
    fbData: List<FBDataItem>,
    showButton :Boolean,
    clickAction:() ->Unit
) {
    val find = fbData.find { it.uid == uid }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (find != null) {
            Text(text = find.displayName ?: "이름없음($uid)")
        } else {
            Text(text = uid)
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showButton) {
            MyIconButton(imageResource = SharedRes.images.room_exit,size =22.dp) {
                clickAction()
            }
        }
    }

}
