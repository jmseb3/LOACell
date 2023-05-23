package com.wonddak.loacell.android.ui.room

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.LoaCellApp

@Composable
fun RoomEnterDialog(
    dismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = dismiss,
        title = {
            Text(text = "어떤 작업을 할까요?")
        },
        text = {
            val user by LoaCellApp.user.collectAsState(null)
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
                )
                Box(
                    modifier =  sameModifier
                ) {
                    EnterButton(
                        sameModifier,
                        SharedRes.images.room_make.drawableResId,
                        "방 만들기",
                        enabled = user != null
                    )
                    if (user == null) {
                        Canvas(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            drawLine(
                                start = Offset(x = canvasWidth, y = 0f),
                                end = Offset(x = 0f, y = canvasHeight),
                                strokeWidth = 5f,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {

        },
        dismissButton = {

        },
        shape = RoundedCornerShape(24.dp),
    )
}

@Composable
fun EnterButton(
    modifier: Modifier,
    @DrawableRes id: Int,
    text: String,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = {},
        modifier = modifier,
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Black
        ),
        enabled = enabled
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