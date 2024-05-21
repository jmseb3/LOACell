package com.wonddak.loacell.android.ui.room.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

//room화면에서 user리스트 화면
@Composable
fun UserView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo = loaCellViewModel.totalRoomInfoValue
    val userList  = totalRoomInfo.userInfoList
    val focusUserName = totalRoomInfo.focusUserName
    Box() {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(userList) { userInfo ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable { loaCellViewModel.setNowUserInfo(userInfo.name) }
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "${userInfo.name} - ${userInfo.representativeCharacter}",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        if (focusUserName.isNotEmpty()) {
            UserFocusView(loaCellViewModel)
        }
    }
}
