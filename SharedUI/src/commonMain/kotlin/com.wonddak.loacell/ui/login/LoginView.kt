package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wonddak.hellogin.apple.AppleLoginButton
import com.wonddak.hellogin.apple.AppleResult
import com.wonddak.hellogin.core.ButtonTheme
import com.wonddak.hellogin.core.Error
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleLoginButton
import com.wonddak.loacell.Const
import com.wonddak.loacell.theme.roboto
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.ui.setting.AppleLoginView
import com.wonddak.loacell.ui.setting.useLinkApple
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.login_anonymous
import loacell.sharedui.generated.resources.login_info_1
import loacell.sharedui.generated.resources.login_info_2
import loacell.sharedui.generated.resources.login_progress
import loacell.sharedui.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginView(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    raidViewModel: RaidViewModel,
) {
    LaunchedEffect(true) {
        raidViewModel.stopObserveRoom()
    }
    LaunchedEffect(authViewModel.initSuccess, authViewModel.user) {
        if (authViewModel.initSuccess && authViewModel.user != null) {
            navController.navigate(Const.NAV_MAIN) {
                popUpTo(Const.NAV_LOGIN) {
                    inclusive = true
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(Res.string.login_info_1),
                        textAlign = TextAlign.Center
                    )
                    Image(
                        painter = painterResource(Res.drawable.logo), contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.login_info_2),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val widthSize = Modifier.fillMaxWidth(0.8f)
                Button(
                    onClick = {
                        authViewModel.launchAnonymousLogin()
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = widthSize,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.login_anonymous),
                        fontFamily = roboto(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = widthSize,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "OR",
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }

                if (useLinkApple) {
                   AppleLoginView(authViewModel.loginHelper)
                }
                Spacer(modifier = Modifier.height(10.dp))
                GoogleLoginButton(
                    tokenResultHandler = authViewModel.googleLoginHandler,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    mode = ButtonTheme.Dark
                )
            }
        }

        if (authViewModel.loginIn) {
            LoadingView(
                info = stringResource(Res.string.login_progress),
                color = Color.Gray.copy(0.5f)
            )
        }
    }
}
