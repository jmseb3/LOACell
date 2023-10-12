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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.dialog.ProfileNameDialog
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus

@Composable
fun LoginInfoView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    val context = LocalContext.current
    val loginHelper = LoginHelper(context)
    val user by loaCellViewModel.user.collectAsState(null)
    val dialogStatus by loaCellViewModel.dialogStatus.collectAsState()

    user?.let { userInfo ->
        var displayName by remember {
            mutableStateOf("")
        }
        LaunchedEffect(true) {
            displayName = user?.displayName ?: ""
        }
        if (dialogStatus == DialogStatus.SETTING_EDIT_NAME) {
            ProfileNameDialog(
                displayName,
                success = {
                    loginHelper.updateDisplayName(it)
                    displayName = it
                    loaCellViewModel.hideDialog()
                },
                dismiss = {
                    loaCellViewModel.hideDialog()
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
                    loaCellViewModel.showDialog(DialogStatus.SETTING_EDIT_NAME)
                }
            }
            Divider()
            val roomList = db.roomInfoQueriesHelper.getAllRoomListByOwnerId(userInfo.uid)

            Row(
                modifier = Modifier.fillMaxWidth().padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val buttonWeight = Modifier.weight(1f)
                OutlinedButton(
                    modifier = buttonWeight,
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
                Spacer(modifier = Modifier.width(15.dp))
                if (userInfo.isAnonymous) {
                    val anonymousToGoogleLoginLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult()
                    ) { result ->
                        loginHelper.registerAnonymousToGoogle(
                            result,
                            failRegisterAction = { error ->
                                loaCellViewModel.showSnackBar(
                                    error.localizedMessage ?: "unknown Error"
                                )
                            }
                        ) { error ->
                            if (error is FirebaseAuthUserCollisionException) {
                                loaCellViewModel.showSnackBar("이미 등록된 계정입니다.")
                            } else {
                                loaCellViewModel.showSnackBar(
                                    error?.localizedMessage ?: "unknown Error"
                                )
                            }
                        }
                    }
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            loginHelper.requestGoogleLogin {
                                anonymousToGoogleLoginLauncher.launch(it)
                            }
                        }
                    ) {
                        Text(text = "Google 계정 연동")
                    }
                } else {
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            loginHelper.delete {
                                loaCellViewModel.signOut()
                            }
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