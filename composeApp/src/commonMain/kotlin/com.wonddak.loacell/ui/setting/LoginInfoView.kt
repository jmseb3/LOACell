package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.ui.modal.dialog.ProfileNameDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.btn_google
import loacell.composeapp.generated.resources.change_person
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginInfoView(
    userInfo: FBUser,
    roomList: List<RoomInfo>,
    updateName: (String) -> Unit,
    outOrSignOut: () -> Unit,
    deleteAccount: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val editNameStatus = rememberModalStatus()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(3.dp)
            ) {
                Text(
                    text = (userInfo.displayName ?: "").ifEmpty { "이름 없음" },
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userInfo.uid,
                    fontSize = 11.sp
                )
            }
            IconButton(
                onClick = {
                    editNameStatus.show()
                }
            ) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    painter = painterResource(Res.drawable.change_person),
                    contentDescription = "change person name",
                )
            }
        }
        HorizontalDivider()

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
                    onClick = outOrSignOut
                ) {
                    Text(text = if (userInfo.isAnonymous) "나가기(탈퇴)" else "로그아웃")
                }
                if (!userInfo.isAnonymous) {
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            if (roomList.isEmpty()) {
                                deleteAccount()
                            } else {
                                showDeleteError = true
                                scope.launch {
                                    delay(1_500L)
                                    showDeleteError = false
                                }
                            }

                        },
                        enabled = !showDeleteError
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
//                                loginHelper.linkToGoogle(
//                                    context as Activity,
//                                    failAction = { msg ->
//                                        loaCellViewModel.showSnackBar(msg)
//                                    },
//                                ) {
//                                    loaCellViewModel.closeSetting()
//                                }
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(Res.drawable.btn_google),
                            contentDescription = "SignInButton",
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
    ProfileNameDialog(
        editNameStatus,
        userInfo.displayName ?: "",
        updateName
    )
}