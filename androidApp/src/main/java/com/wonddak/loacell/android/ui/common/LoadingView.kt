package com.wonddak.loacell.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wonddak.loacell.android.noRippleClickable

@Composable
fun LoadingView(
    info :String = ""
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Gray.copy(0.5f)).noRippleClickable(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(text = info)
    }
}