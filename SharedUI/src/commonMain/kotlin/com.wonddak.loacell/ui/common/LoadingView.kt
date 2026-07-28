package com.wonddak.loacell.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.noRippleClickable

@Composable
fun LoadingView(
    info: String = "",
    color: Color = Color.White
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color).noRippleClickable(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        if (info.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(text = info)
        }
    }
}