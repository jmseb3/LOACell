package com.wonddak.loacell.android.ui.room.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getAllUidList
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.sharedapi.FBApi
import com.wonddak.sharedapi.FBDataItem
import com.wonddak.sharedapi.FBRequest
import kotlinx.coroutines.delay

@Composable
fun SettingRoomView(
    loaCellViewModel: LoaCellViewModel
) {
    val roomInfo by loaCellViewModel.roomInfo.collectAsState()
    roomInfo?.let { info ->
//        UserUidList(roomInfo = info)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserUidList(
    roomInfo: RoomInfo
) {
    val group = mapOf(
        RoomRole.OWNER.toName to listOf(roomInfo.owner),
        RoomRole.MANAGER.toName to roomInfo.editableUser,
        RoomRole.USER.toName to roomInfo.enterUser,
        RoomRole.ANONYMOUS.toName to roomInfo.anonymousUser
    )
    var fetch by remember {
        mutableStateOf(false)
    }
    var result: List<FBDataItem> by remember {
        mutableStateOf(listOf())
    }
    LaunchedEffect(true) {
        fetch = true
        val api = FBApi()
        api.getData(FBRequest(roomInfo.getAllUidList())).let { fbData ->
            result = fbData.data

            val failUser = fbData.failUidList
            val anonymousUser = roomInfo.anonymousUser.filter { failUser.contains(it) }
            val editableUser = roomInfo.editableUser.filter { failUser.contains(it) }
            val enterUser = roomInfo.enterUser.filter { failUser.contains(it) }

            var result1 = false
            var result2 = false
            var result3 = false

            CommonRoomHelper.exitUsersFromRoom(
                roomId = roomInfo.uniqueId,
                userId = anonymousUser,
                field = "anonymousUser",
                commonAction = {
                    result1 = true
                }
            )
            CommonRoomHelper.exitUsersFromRoom(
                roomId = roomInfo.uniqueId,
                userId = editableUser,
                field = "editableUser",
                commonAction = {
                    result2 = true
                }
            )
            CommonRoomHelper.exitUsersFromRoom(
                roomId = roomInfo.uniqueId,
                userId = enterUser,
                field = "enterUser",
                commonAction = {
                    result3 = true
                }
            )

            while (!result1 || !result2 || !result3) {
                delay(1_000L)
            }
            fetch = false
        }
    }

    if (!fetch) {
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
                            fontSize = 20.sp
                        )
                    }
                    items(filter) { id ->
                        UserUidItem(uid = id, fbData = result)
                    }
                }
            }
        }
    } else {
        LoadingView("유저 정보를 가져옵니다.")
    }
}

@Composable
fun UserUidItem(
    uid: String,
    fbData: List<FBDataItem>
) {
    val find = fbData.find { it.uid == uid }
    if (find != null) {
        Text(text = find.displayName ?: "이름없음($uid)")
    } else {
        Text(text = uid)
    }
}

@Composable
fun SettingRoom() {

}