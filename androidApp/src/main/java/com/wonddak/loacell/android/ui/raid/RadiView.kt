package com.wonddak.loacell.android.ui.raid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo

@Composable
fun RaidView(
    roomInfo: RoomInfo,
    raidInfoList : List<RaidInfo>
) {
    Column {
        Text(text = "SelectRoomId : ${roomInfo.id}")
        Text(text = "title : ${roomInfo.title}")
        Text(text = "description : ${roomInfo.description}")
        Divider()
        LazyColumn {
            items(raidInfoList) {raidInfo ->
                RaidItemRow(raidInfo)
            }
        }
    }
}

@Composable
@Preview
fun RaidViewPreView() {
    RaidView(
        roomInfo = RoomInfo(1L,"title","description"),
        raidInfoList = emptyList()
    )

}

@Composable
fun RaidItemRow(raidInfo: RaidInfo) {
    Text(text = raidInfo.type.toString())
}
