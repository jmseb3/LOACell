package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.android.R
import com.wonddak.loacell.android.roboto
import com.wonddak.loacell.android.util.LoginHelper

@Composable
fun LoginView() {
    val context = LocalContext.current
    val loginHelper = LoginHelper(context)

    val googleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loginHelper.registerGoogleToken(result)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        GoogleLoginButton(
            action = {
                loginHelper.requestGoogleLogin { intent ->
                    googleLoginLauncher.launch(intent)
                }
            }
        )
//        LoginButton(
//            action = { loginHelper.requestAnonymousLogin() }
//        ) {
//            Text(
//                text = "로그인 하지 않기",
//                color = Color.Black
//            )
//        }
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun LoginViewPreview() {
    LoginView()
}

@Composable
fun GoogleLoginButton(
    action: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp)
            .clickable {
                action()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
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
        }
    }
}