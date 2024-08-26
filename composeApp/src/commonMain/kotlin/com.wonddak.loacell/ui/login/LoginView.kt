package com.wonddak.loacell.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wonddak.loacell.Const
import com.wonddak.loacell.auth.rememberAuthLauncher
import com.wonddak.loacell.theme.roboto
import com.wonddak.loacell.ui.common.LoadingView
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.btn_google
import loacell.composeapp.generated.resources.login_anonymous
import loacell.composeapp.generated.resources.login_info_1
import loacell.composeapp.generated.resources.login_info_2
import loacell.composeapp.generated.resources.login_progress
import loacell.composeapp.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun LoginView(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    storeViewModel: StoreViewModel,
) {
    LaunchedEffect(true) {
        storeViewModel.stopObserveRoom()
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
    val authLauncher = rememberAuthLauncher(authViewModel.loginHelper) {

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
                        authLauncher.launchAnonymousLogin()
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

                Spacer(modifier = Modifier.height(10.dp))

                GoogleLoginButton(
                    modifier = widthSize
                ) {
                    authLauncher.launchGoogleLogin()
                }
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

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    action: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(40.dp)
            .clickable {
                action()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            contentColor = Color.Black,
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(Res.drawable.btn_google),
                contentDescription = "SignInButton",
                tint = Color.Unspecified
            )
            Text(
                text = "Sign in with Google",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.54f),
                fontFamily = roboto(),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}