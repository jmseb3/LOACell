package com.wonddak.loacell.android.ui.setting

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus

@Composable
fun LoginInfoView(
    loaCellViewModel: LoaCellViewModel
) {
    val loginHelper = loaCellViewModel.loginHelper
    val user by loaCellViewModel.user.collectAsState(null)
    val roomLists by loaCellViewModel.roomList.collectAsState()
    val roomList = roomLists.filter { it.owner == user?.uid }

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
                        text = (user?.displayName ?: "") .ifEmpty{ "이름 없음" },
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
                Spacer(modifier = Modifier.width(15.dp))
                if (userInfo.isAnonymous) {
                    val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult()
                    ) { result ->
                        loginHelper.registerAnonymousToGoogle(result) {
                            loaCellViewModel.showSnackBar(it)
                        }
                    }
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            loginHelper.requestGoogleLogin(anonymousToGoogleLoginLauncher)
                        }
                    ) {
                        Text(text = "Google 계정 연동")
                    }
                } else {
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            loginHelper.delete()
                        },
                        enabled = roomList.isEmpty()
                    ) {
                        Text(text = "탈퇴")
                    }
                }
            }

            if (!userInfo.isAnonymous && roomList.isNotEmpty()) {
                Text(text = "소유자인 방의 정보를 모두 삭제해 주세요")
            }
        }
    }
}