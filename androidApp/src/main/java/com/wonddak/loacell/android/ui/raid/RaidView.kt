package com.wonddak.loacell.android.ui.raid

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.android.MyApplicationTheme
import com.wonddak.loacell.room.RoomInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RaidView(
    roomList: List<RoomInfo>,
    addRoomAction: () -> Unit = {},
    showRoomInfo:(roomId:Long) -> Unit = {}
) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .shadow(1.dp, spotColor = Color.Gray)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Room Info")
            OutlinedButton(onClick = { addRoomAction() }) {
                Text(text = "ADD")
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
            items(roomList) { roomInfo ->
                RoomInfoRow(roomInfo) { showRoomInfo(roomInfo.id) }
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Preview
@Composable
fun RaidViewPreview() {
    val testList: List<RoomInfo> = listOf(
        RoomInfo(1, "test1", "여기는 1 이다."),
        RoomInfo(2, "test2", "여기는 2 이다."),
        RoomInfo(3, "test3", "여기는 3 이다."),
    )
    RaidView(testList)
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
            fontSize = 18.sp
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = room.description,
            fontSize = 14.sp
        )
    }
}