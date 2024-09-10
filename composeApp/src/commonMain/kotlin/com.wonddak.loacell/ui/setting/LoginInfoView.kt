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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.auth.AppleLoginGuide
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.rememberModalStatus
import com.wonddak.loacell.theme.roboto
import com.wonddak.loacell.ui.modal.dialog.ProfileNameDialog
import com.wonddak.loacell.viewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.btn_google
import loacell.composeapp.generated.resources.change_person
import loacell.composeapp.generated.resources.logo_apple
import org.jetbrains.compose.resources.painterResource

internal expect val useLinkApple: Boolean

@Composable
fun LoginInfoView(
    authViewModel: AuthViewModel,
    userInfo: FBUser,
    roomList: List<RoomInfo>,
    appleLoginGuide: AppleLoginGuide? = null,
    onBack: () -> Unit,
    showSnackbar: (String) -> Unit,
) {
    val isAppleProvider = authViewModel.checkAppleProvider()
    val scope = rememberCoroutineScope()
    val editNameStatus = rememberModalStatus()
    val appleLoginGuideImpl by remember {
        mutableStateOf(appleLoginGuide)
    }
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
                            if (roomList.isEmpty()) {
                                if (isAppleProvider) {
                                    appleLoginGuideImpl?.revokeToken(
                                        fail = {

                                        },
                                        success = {
                                            authViewModel.deleteAccount()
                                            onBack()
                                        }
                                    )
                                } else {
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
                    IconButton(
                        onClick = {
                            authViewModel.linkToGoogleAccount(
                                failAction = {
                                    showSnackbar(it)
                                },
                                successAction = {
                                    onBack()
                                }
                            )
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            painter = painterResource(Res.drawable.btn_google),
                            contentDescription = "Google Link Button",
                            tint = Color.Unspecified
                        )
                    }
                    if (useLinkApple) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    appleLoginGuideImpl?.linkToApple(
                                        fail = {
                                            showSnackbar(it)
                                        },
                                        success = {
                                            onBack()
                                        }
                                    )
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(36.dp),
                                painter = painterResource(Res.drawable.logo_apple),
                                contentDescription = "Apple Link Button",
                                tint = Color.Unspecified
                            )
                        }
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