package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.hellogin.core.ButtonType
import com.wonddak.hellogin.core.Error
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleLoginButton
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.auth.registerAnonymousToGoogle
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.theme.roboto
import com.wonddak.loacell.ui.modal.dialog.ProfileNameDialog
import com.wonddak.loacell.viewModel.AuthViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.change_person
import org.jetbrains.compose.resources.painterResource

internal expect val useLinkApple: Boolean
@Composable
expect fun AppleLoginView(
    loginHelper: LoginHelper,
)

@Composable
expect fun AppleLoginBtn(
    loginHelper: LoginHelper,
    onSuccess: () -> Unit,
    onFail: (msg: String) -> Unit
)

expect fun revokeApple(
    loginHelper: LoginHelper,
    scope: CoroutineScope,
    onSuccess: () -> Unit
)

@Composable
fun LoginInfoView(
    authViewModel: AuthViewModel,
    userInfo: FBUser,
    roomList: List<RoomInfo>,
    onBack: () -> Unit,
    showSnackbar: (String) -> Unit,
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
                    onClick = {
                        authViewModel.outOrSignOut()
                        onBack()
                    }
                ) {
                    Text(text = if (userInfo.isAnonymous) "나가기(탈퇴)" else "로그아웃")
                }
                if (!userInfo.isAnonymous) {
                    Spacer(modifier = Modifier.width(15.dp))
                    OutlinedButton(
                        modifier = buttonWeight,
                        onClick = {
                            //탈퇴하기
                            if (roomList.isEmpty()) {
                                if (authViewModel.checkAppleProvider()) {
                                    Napier.d { "This is Apple Acc" }
                                    revokeApple(
                                        loginHelper = authViewModel.loginHelper,
                                        scope = scope
                                    ) {
                                        onBack()
                                    }
                                } else {
                                    Napier.d { "This is not Apple Acc" }
                                    authViewModel.deleteAccount()
                                    onBack()
                                }
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
                Text(
                    text = "소유자인 방의 정보를 모두 삭제해 주세요",
                    fontFamily = roboto()
                )
            }
            if (userInfo.isAnonymous) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "연동하기",
                        textAlign = TextAlign.Center,
                        fontFamily = roboto()
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val googleLinkHandler = remember {
                        object : TokenResultHandler<GoogleResult> {
                            override fun onFail(error: Error?) {
                                showSnackbar(error.toString())
                            }

                            override fun onSuccess(token: GoogleResult) {
                                authViewModel.loginHelper.registerAnonymousToGoogle(
                                    result = token,
                                    failAction = {
                                        showSnackbar(it)
                                    },
                                    successAction = {
                                        onBack()
                                    }
                                )
                            }
                        }
                    }
                    GoogleLoginButton(
                        googleLinkHandler,
                        type = ButtonType.IconOnly
                    )
                    if (useLinkApple) {
                        AppleLoginBtn(
                            loginHelper = authViewModel.loginHelper,
                            onSuccess = {
                                onBack()
                            },
                            onFail = {
                                showSnackbar(it)
                            }
                        )
                    }
                }
            }
        }
    }
    ProfileNameDialog(
        editNameStatus,
        userInfo.displayName ?: "",
        authViewModel::updateName
    )
}