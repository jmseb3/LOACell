package com.wonddak.loacell.android.ui.login

import android.app.Activity
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.android.toText
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.theme.roboto
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.auth.requestAnonymousLogin
import com.wonddak.sharedresources.store.CommonString
import kotlinx.coroutines.launch

@Composable
fun LoginView(loaCellViewModel: LoaCellViewModel) {
    val loginHelper =  LoaCellApp.loginHelper
    val loggingIn by loginHelper.loginIn.collectAsState()
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
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
                        text = CommonString.Login.getInfo1().toText(),
                        textAlign = TextAlign.Center
                    )
                    Image(painter = painterResource(id = SharedRes.images.logo.drawableResId), contentDescription = null)
                    Text(
                        text = CommonString.Login.getInfo2().toText(),
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
                        loginHelper.requestAnonymousLogin()
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = widthSize,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = CommonString.Login.getAnonymous().toText(),
                        fontFamily = roboto,
//                        color = Color.White,
//                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = widthSize,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "OR",
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Divider(
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                GoogleLoginButton(
                    modifier = widthSize
                ) {
                    scope.launch {
                        loginHelper.requestGoogleLogin(context as Activity) { result ->
                            loaCellViewModel.syncStartForce(result.user!!.uid)
                        }
                    }
                }
            }
        }
        if (loggingIn) {
            LoadingView(info = CommonString.Login.getProgress().toText(), color = Color.Gray.copy(0.5f))
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
                painter = painterResource(id = SharedRes.images.btn_google.drawableResId),
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