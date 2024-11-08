package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wonddak.loacell.SetBackAction
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.ui.common.SectionCardView
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FBStorageUtil
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.util.UrlList
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SettingView(
    splashViewModel: SplashViewModel,
    authViewModel: AuthViewModel,
    roomList: List<RoomInfo>,
    onBack: () -> Unit,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    val fileHelper: FileHelper = koinInject()
    SetBackAction(true) {
        onBack()
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            LoaCellTopAppBar("설정") {
                if (!navController.popBackStack()) {
                    onBack()
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "settingMain",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("settingMain") {
                SettingMainView(
                    authViewModel,
                    roomList,
                    onBack,
                    showSnackBar = { msg ->
                        scope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(msg, actionLabel = "확인")
                        }
                    },
                    navigationAsset = {
                        navController.navigate("settingAsset") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("settingAsset") {
                AssetFileView(
                    splashViewModel,
                    showProgressWithReDownload = { name ->
                        navController.navigate(
                            "settingProgress/$name"
                        )
                    }
                )
            }
            dialog(
                route = "settingProgress/{name}",
                dialogProperties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                arguments = listOf(
                    navArgument("name") {
                        // Make argument type safe
                        type = NavType.StringType
                    }
                )
            ) { entry -> // NavBackStackEntry
                val fileName = entry.arguments?.getString("name")!!
                Column(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    var title by remember {
                        mutableStateOf("")
                    }
                    LaunchedEffect(true) {
                        title = "파일 삭제 요청"
                        delay(1000)
                        title = "파일 삭제중"
                        val result = fileHelper.deleteAssetFile(fileName)
                        delay(1000)
                        if (result) {
                            title = "다운로드 요청중"
                            delay(1000L)
                            FBStorageUtil.downloadFile(
                                fileName = fileName,
                                fileHelper = fileHelper,
                                successAction = {
                                    title = "다운로드 성공"
                                },
                                failAction = {
                                    title = "다운로드 실패"
                                }
                            )
                        } else {
                            title = "파일 삭제에 실패 했습니다."
                            delay(1000)
                            navController.popBackStack()
                        }
                    }

                    LaunchedEffect(title) {
                        if (title == "다운로드 성공") {
                            delay(1000L)
                            navController.popBackStack()
                        }
                    }

                    CircularProgressIndicator()
                    Text(title)

                }
            }
        }
    }
}

@Composable
fun SettingMainView(
    authViewModel: AuthViewModel,
    roomList: List<RoomInfo>,
    onBack: () -> Unit,
    showSnackBar: (msg: String) -> Unit,
    navigationAsset: () -> Unit
) {
    var showMenu by remember {
        mutableStateOf(false)
    }

    val config: Config = koinInject()
    val scope = rememberCoroutineScope()
    val webLauncher = rememberWebLauncher()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)
    ) {
        authViewModel.user?.let { userInfo ->
            SectionCardView(title = "로그인 정보") {
                LoginInfoView(
                    authViewModel,
                    userInfo,
                    roomList.filter { it.owner == userInfo.uid },
                    onBack,
                    showSnackbar = showSnackBar
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
                                showMenu = false
                                showSnackBar("$name 사이트로 변경 되었습니다.")
                            }
                        }
                    )
                }
            }
        }
        SectionText(title = "버그 제보 및 건의하기") {
            webLauncher.launchWeb("https://discord.gg/acD6rQ9Tja")
        }

        SectionText("Asset 파일 관리") {
            navigationAsset()
        }

        SectionText(title = "앱 버전 : ${getAppVersion()}")
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
                .padding(vertical = 10.dp, horizontal = 5.dp)
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