package com.wonddak.loacell.android.ui.room.setting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun SettingRoomView (
    loaCellViewModel: LoaCellViewModel
) {
    val roomInfo by loaCellViewModel.roomInfo.collectAsState()
    roomInfo?.let {info ->
        UserUidList(roomInfo = info)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserUidList(
    roomInfo: RoomInfo
) {
    val group = mapOf(
        RoomRole.OWNER.toName to roomInfo.owner,
        RoomRole.MANAGER.toName to roomInfo.editableUser,
        RoomRole.USER.toName to roomInfo.enterUser,
        RoomRole.ANONYMOUS.toName to roomInfo.anonymousUser
    )

    LazyColumn {
        group.forEach { (name, data) ->
            stickyHeader {
                Text(
                    text = name,
                    modifier = Modifier.fillMaxWidth().background(Color.Gray),
                    fontSize = 20.sp
                )
            }
            if (data is String) {
                item(data) {
                    Text(text = data)
                }
            } else {
                data as List<String>
                items(data) { id ->
                    Text(text = id)
                }
            }
        }
    }
}