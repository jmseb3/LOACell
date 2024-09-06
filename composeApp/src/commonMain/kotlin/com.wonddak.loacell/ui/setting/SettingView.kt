package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.ui.common.SectionCardView
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.UrlList
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SettingView(
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
    onBack: () -> Unit,
) {
    var showMenu by remember {
        mutableStateOf(false)
    }
    val config: Config = koinInject()
    val scope = rememberCoroutineScope()
    val webLauncher = rememberWebLauncher()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    SetBackAction(true) {
        onBack()
    }
    Scaffold(
        topBar = {
            LoaCellTopAppBar("설정") {
                onBack()
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 10.dp)
        ) {
            authViewModel.user?.let { userInfo ->
                SectionCardView(title = "로그인 정보") {
                    LoginInfoView(
                        userInfo,
                        storeViewModel.roomList.filter { it.owner == userInfo.uid },
                        authViewModel::updateName,
                        {
                            onBack()
                            authViewModel.outOrSignOut()
                        },
                        authViewModel::deleteAccount,
                    )
                }
            }

            Box {
                SectionText(
                    title = "검색 사이트 변경"
                ) {
                    showMenu = true
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    UrlList.forEach { (name, url) ->
                        DropdownMenuItem(
                            text = {
                                Text(text = name)
                            },
                            onClick = {
                                scope.launch {
                                    config.updateDefaultUrl(url)
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    showMenu = false
                                    snackbarHostState.showSnackbar(
                                        "$name 사이트로 변경 되었습니다.",
                                        actionLabel = "확인"
                                    )
                                }
                            }
                        )
                    }
                }
            }
            SectionText(title = "버그 제보 및 건의하기") {
                webLauncher.launchWeb("https://discord.gg/acD6rQ9Tja")
            }
            SectionText(title = "앱 버전 : ${getAppVersion()}")
        }
    }
}

@Composable
internal fun SectionText(
    title: String,
    useDivider: Boolean = true,
    action: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .noRippleClickable {
                    action?.invoke()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title)
            if (action != null) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                )
            }
        }
        if (useDivider) {
            HorizontalDivider()
        }
    }
}

expect fun getAppVersion(): String