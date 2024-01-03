package com.wonddak.loacell.android.ui.setting

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.R
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.auth.delete
import com.wonddak.loacell.model.DialogStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginInfoView(
    loaCellViewModel: LoaCellViewModel
) {
    val loginHelper = loaCellViewModel.loginHelper
    val user by loaCellViewModel.user.collectAsState(null)
    val roomLists by loaCellViewModel.roomList.collectAsState()
    val roomList = roomLists.filter { it.owner == user?.uid }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    user?.let { userInfo ->
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = (userInfo.displayName ?: "").ifEmpty { "이름 없음" },
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userInfo.uid
                    )
                }
                MyIconButton(imageResource = SharedRes.images.change_person) {
                    loaCellViewModel.showDialog(DialogStatus.SETTING_EDIT_NAME)
                }
            }
            Divider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                var showDeleteError by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val buttonWeight = Modifier.weight(1f)
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            loaCellViewModel.outOrSignOut()
                        }
                    ) {
                        Text(text = if (userInfo.isAnonymous) "나가기" else "로그아웃")
                    }
                    if (!userInfo.isAnonymous) {
                        Spacer(modifier = Modifier.width(15.dp))
                        OutlinedButton(
                            modifier = buttonWeight,
                            onClick = {
                                if (roomList.isEmpty()) {
                                    loginHelper.delete()
                                } else {
                                    showDeleteError = true
                                    scope.launch {
                                        delay(1_500L)
                                        showDeleteError = false
                                    }
                                }

                            },
                            enabled = roomList.isEmpty() && !showDeleteError
                        ) {
                            Text(text = "탈퇴")
                        }
                    }
                }
                if (showDeleteError) {
                    Text(text = "소유자인 방의 정보를 모두 삭제해 주세요")
                }
                if (userInfo.isAnonymous) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "연동하기",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = {
                                scope.launch {
                                    loginHelper.linkToGoogle(
                                        context as Activity,
                                        failAction = { msg ->
                                            loaCellViewModel.showSnackBar(msg)
                                        },
                                    ) {
                                        loaCellViewModel.closeSetting()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(36.dp),
                                painter = painterResource(id = R.drawable.btn_google),
                                contentDescription = "SignInButton",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}