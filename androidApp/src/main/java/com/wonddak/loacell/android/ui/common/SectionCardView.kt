package com.wonddak.loacell.android.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionCardView(
    title: String? = null,
    @DrawableRes icon: Int = 0,
    iconAction: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box() {
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        fontSize = 20.sp
                    )
                }
                if (icon != 0) {
                    MyIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        id = icon,
                        size = 18.dp
                    ) {
                        iconAction()
                    }
                }
            }
            if (title != null || icon != 0) {
                Divider()
            }
            content()
        }
    }
}