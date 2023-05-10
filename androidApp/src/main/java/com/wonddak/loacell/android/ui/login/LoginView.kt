package com.wonddak.loacell.android.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.android.R
import com.wonddak.loacell.android.util.LoginHelper

@Composable
fun LoginView(
    loginHelper: LoginHelper,
) {
    val googleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        loginHelper.registerGoogleToken(result)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        val modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
        LoginButton(
            modifier,
            {
                loginHelper.requestGoogleLogin { intent ->
                    googleLoginLauncher.launch(intent)
                }
            }
        ) {
            Box(
                modifier.padding(horizontal = 8.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.btn_google),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "SIGN IN WITH GOOGLE",
                    fontSize = 14.sp
                )
            }
        }
        LoginButton(
            modifier,
            { loginHelper.requestAnonymousLogin() }
        ) {
            Text(text = "로그인 하지 않기")
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun LoginViewPreview() {
    val context = LocalContext.current
    LoginView(LoginHelper((context)))
}

@Composable
fun LoginButton(
    modifier: Modifier = Modifier,
    action: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Button(
        onClick = { action() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        content()
    }

}