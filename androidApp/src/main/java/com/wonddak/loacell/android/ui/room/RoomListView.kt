package com.wonddak.loacell.android.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RoomInfo

@Composable
fun RooListView(
    roomList: List<RoomInfo>,
    showRoomInfo:(roomId:String) -> Unit = {}
) {
    LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
        items(roomList) { roomInfo ->
            RoomInfoRow(roomInfo) { showRoomInfo(roomInfo.uniqueId) }
            Divider()
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
fun RoomViewPreview() {
    val testList: List<RoomInfo> = listOf(
        RoomInfo("!2312312", "test1", "여기는 1 이다."),
        RoomInfo("%4534534534", "test2", "여기는 2 이다."),
        RoomInfo("#$534534534", "test3", "여기는 3 이다."),
    )
    RooListView(testList)
}

@Composable
fun RoomInfoRow(
    room: RoomInfo,
    moveToInfo:() -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10))
            .background(Color.Gray)
            .clickable {
                moveToInfo()
            }
            .padding(5.dp)
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