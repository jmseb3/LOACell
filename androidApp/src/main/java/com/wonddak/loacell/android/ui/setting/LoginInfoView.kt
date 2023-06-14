package com.wonddak.loacell.android.ui.setting

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.dialog.ProfileNameDialog
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun LoginInfoView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val context = LocalContext.current
    val loginHelper = LoginHelper(context)
    val user by loaCellViewModel.user.collectAsState(null)
    user?.let { userInfo ->
        var displayName by remember {
            mutableStateOf("")
        }
        LaunchedEffect(true) {
            displayName = user?.displayName ?:""
        }
        if (loaCellViewModel.showSettingEditName) {
            ProfileNameDialog(
                displayName,
                success = {
                    loginHelper.updateDisplayName(it)
                    displayName = it
                    loaCellViewModel.showSettingEditName = false
                },
                dismiss = {
                    loaCellViewModel.showSettingEditName = false
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = displayName.ifEmpty { "이름 없음" },
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userInfo.uid
                    )
                }
                MyIconButton(imageResource = SharedRes.images.change_person) {
                    loaCellViewModel.showSettingEditName = true
                }
            }
            Divider()
            OutlinedButton(
                onClick = {
                    if (userInfo.isAnonymous) {
                        loginHelper.delete {
                            loaCellViewModel.signOut()
                        }
                    } else {
                        loginHelper.signOut()
                        loaCellViewModel.signOut()
                    }
                }
            ) {
                Text(text = if (userInfo.isAnonymous) "나가기" else "로그아웃")
            }
            if (userInfo.isAnonymous) {
                val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult()
                ) { result ->
                    loginHelper.registerAnonymousToGoogle(result) { error ->
                        if (error is FirebaseAuthUserCollisionException) {
                            loaCellViewModel.showSnackBar("이미 등록된 계정입니다.")
                        } else {
                            loaCellViewModel.showSnackBar(
                                error?.localizedMessage ?: "unknown Error"
                            )
                        }
                    }
                }
                OutlinedButton(onClick = {
                    loginHelper.requestGoogleLogin {
                        anonymousToGoogleLoginLauncher.launch(it)
                    }
                }) {
                    Text(text = "Google 계정 연동")
                }
            } else {
                val list = db.roomInfoQueriesHelper.getAllRoomListByOwnerId(userInfo.uid)
                Row() {
                    OutlinedButton(
                        onClick = {
                            loginHelper.delete {
                                loaCellViewModel.signOut()
                            }
                        },
                        enabled = list.isEmpty()
                    ) {
                        Text(text = "탈퇴")
                    }
                    if (list.isNotEmpty()) {
                        Text(text = "소유자인 방의 정보를 모두 삭제해 주세요")
                    }
                }
            }
        }
    }
}