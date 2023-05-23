package com.wonddak.loacell.android.ui.room

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.LoaCellApp
import kotlinx.coroutines.delay

@Composable
fun RoomEnterDialog(
    confirm: () -> Unit,
    dismiss: () -> Unit,
) {
    val user by LoaCellApp.user.collectAsState(null)
    var status by remember {
        mutableStateOf(0)
    }

    AlertDialog(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = dismiss,
        title = {
            val title = when(status) {
                2 -> "방 만들기"
                1 -> "입장하기"
                else -> "작업을 선택해 주세요"
            }
            Text(text = title)
        },
        text = {
            AnimatedVisibility(status == 0) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var errorMsg :String by remember {
                        mutableStateOf("")
                    }
                    LaunchedEffect(errorMsg) {
                        if (errorMsg.isNotEmpty()) {
                            delay(1_000L)
                            errorMsg = ""
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val sameModifier = Modifier.size(100.dp)
                        EnterButton(
                            sameModifier,
                            SharedRes.images.room_enter.drawableResId,
                            "입장하기"
                        ) {
                            status = 1
                        }
                        Box(
                            modifier =  sameModifier
                        ) {
                            EnterButton(
                                sameModifier,
                                SharedRes.images.room_make.drawableResId,
                                "방 만들기",
                            ) {
                                if (user!=null) {
                                    status = 2
                                } else {
                                    errorMsg = "로그인한 사용자만 가능한 기능입니다."
                                }

                            }
                            if (user == null) {
                                Canvas(modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))) {
                                    val canvasWidth = size.width
                                    val canvasHeight = size.height
                                    drawLine(
                                        start = Offset(x = canvasWidth, y = 0f),
                                        end = Offset(x = 0f, y = canvasHeight),
                                        strokeWidth = 5f,
                                        color = Color.Red.copy(0.6f)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorMsg,
                        textAlign = TextAlign.Center
                    )
                }
            }
            AnimatedVisibility(status == 1) {
                Text(text = "입장")
            }
            AnimatedVisibility(status == 2) {
                Text(text = "만들 ")
            }
        },
        confirmButton = {
            if(status != 0) {
                TextButton(
                    onClick = {
                        confirm()
                    },
                ) {
                    Text(if (status ==1) "입장" else "생성")
                }
            }
        },
        dismissButton = {
            if(status != 0) {
                TextButton(
                    onClick = dismiss
                ) {
                    Text("취소")
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
    )
}

@Composable
fun EnterButton(
    modifier: Modifier,
    @DrawableRes id: Int,
    text: String,
    onClick:() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(painter = painterResource(id = id), contentDescription = null)
            Text(text = text)
        }
    }

}