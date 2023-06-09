package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.wonddak.database.AppDataBase
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
        Column() {
            Text(text = userInfo.uid)
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
                    Text(text = "Google과 연동")
                }
            } else {
                val list = db.roomInfoQueriesHelper.getAllRoomListByOwnerId(userInfo.uid)
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
                    Text(text = "owner인 방의 정보를 모두 삭제해 주세요")
                }
            }
        }
    }
}