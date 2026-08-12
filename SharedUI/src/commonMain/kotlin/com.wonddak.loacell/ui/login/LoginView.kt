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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.ui.setting.AppleLoginView
import com.wonddak.loacell.ui.setting.useLinkApple
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.login_anonymous
import loacell.sharedui.generated.resources.login_info_1
import loacell.sharedui.generated.resources.login_info_2
import loacell.sharedui.generated.resources.login_progress
import loacell.sharedui.generated.resources.login_progress_description
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
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
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
                    modifier = widthSize,
                ) {
                    Text(
                        text = stringResource(Res.string.login_anonymous),
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
                        text = "또는",
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
                GoogleSignInButton(
                    onClick = authViewModel::requestGoogleLogin,
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            }
        }

        if (authViewModel.loginIn) {
            LoginLoadingOverlay(
                title = stringResource(Res.string.login_progress),
                description = stringResource(Res.string.login_progress_description),
            )
        }
    }
}
