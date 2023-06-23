package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.android.R
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.theme.roboto
import com.wonddak.loacell.android.util.LoginHelper
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun LoginView(loaCellViewModel: LoaCellViewModel) {
    val context = LocalContext.current
    val loginHelper = LoginHelper(context)

    val googleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loaCellViewModel.apply {
            loginHelper.registerGoogleToken(
                result,
                commonAction = {
                    println("<>>>>>>>>>>> common")
                    loggingIn = true
                },
                failRegisterAction = {e ->
                    loggingIn = false
                },
                successAction = {
                    syncStart()
                    loggingIn = false
                },
                failAction = {
                    loggingIn = false
                }
            )
        }

    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier =Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "레이드 관리를 도와 주는 ",
                        textAlign = TextAlign.Center
                    )
                    Image(painter = painterResource(id = R.mipmap.ic_launcher_foreground), contentDescription = null)
                    Text(
                        text = "입니다.",
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
                GoogleLoginButton(
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    loginHelper.requestGoogleLogin { intent ->
                        googleLoginLauncher.launch(intent)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "로그인 하지 않고 계속",
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .noRippleClickable { loginHelper.requestAnonymousLogin() }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        if (loaCellViewModel.loggingIn) {
            LoadingView(info = "로그인 처리 중입니다.", color = Color.Gray.copy(0.5f))
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
                painter = painterResource(id = R.drawable.btn_google),
                contentDescription = "SignInButton",
                tint = Color.Unspecified
            )
            Text(
                text = "Sign in with Google",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.54f),
                fontFamily = roboto,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}