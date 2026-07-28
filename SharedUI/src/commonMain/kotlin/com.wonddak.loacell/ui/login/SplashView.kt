package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.SplashViewModel
import kotlinx.coroutines.delay
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashView(
    splashViewModel: SplashViewModel,
    authViewModel: AuthViewModel,
    goToMain: () -> Unit,
    goToLogin: () -> Unit,
) {
    LaunchedEffect(true) {
        splashViewModel.startCheck()
    }
    var needDownload by remember {
        mutableStateOf(false)
    }

    fun navigationTo() {
        splashViewModel.readAssetsFile()
        if (authViewModel.initSuccess) {
            if (authViewModel.user == null) {
                goToLogin()
            } else {
                goToMain()
            }
        }
    }
    LaunchedEffect(splashViewModel.timeCheck, splashViewModel.downloadCheck) {
        if (splashViewModel.timeCheck && splashViewModel.downloadCheck) {
            if (splashViewModel.downloadFileSet.isEmpty()) {
                navigationTo()
            } else {
                needDownload = true
                splashViewModel.startDownload()
            }
        }
    }
    LaunchedEffect(splashViewModel.maxCnt, splashViewModel.successCnt) {
        if (splashViewModel.maxCnt > 0 && splashViewModel.maxCnt == splashViewModel.successCnt) {
            delay(500L)
            navigationTo()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            null,
            Modifier.align(Alignment.Center)
        )

        if (needDownload) {
            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(10.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "리소스 파일을 다운로드 중입니다.(${splashViewModel.successCnt}/${splashViewModel.maxCnt})",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
