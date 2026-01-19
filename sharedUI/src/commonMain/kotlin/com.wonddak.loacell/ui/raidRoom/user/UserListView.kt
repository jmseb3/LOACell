package com.wonddak.loacell.ui.raidRoom.user

import androidx.compose.foundation.clickable
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
import com.wonddak.loacell.model.UserInfo

@Composable
fun UserListView(
    userList: List<UserInfo>,
    navigation: (UserInfo) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(userList) { userInfo ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { navigation(userInfo) }
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
}