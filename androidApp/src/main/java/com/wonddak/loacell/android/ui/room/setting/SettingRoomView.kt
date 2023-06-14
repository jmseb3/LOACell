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
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getAllUidList
import com.wonddak.sharedapi.FBApi
import com.wonddak.sharedapi.FBDataItem
import com.wonddak.sharedapi.FBRequest

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
        RoomRole.OWNER.toName to listOf(roomInfo.owner),
        RoomRole.MANAGER.toName to roomInfo.editableUser,
        RoomRole.USER.toName to roomInfo.enterUser,
        RoomRole.ANONYMOUS.toName to roomInfo.anonymousUser
    )
    var result : List<FBDataItem> by remember {
        mutableStateOf(listOf())
    }
    LaunchedEffect(true) {
        val api = FBApi()
        result = api.getData(FBRequest(roomInfo.getAllUidList())).items
    }

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
}
@Composable
fun UserUidItem(
    uid :String,
    fbData: List<FBDataItem>
) {
    val find = fbData.find { it.uid == uid }
    if (find != null) {
        Text(text = find.displayName?: "이름없음($uid)")
    } else {
        Text(text = uid)
    }
}

@Composable
fun SettingRoom() {

}