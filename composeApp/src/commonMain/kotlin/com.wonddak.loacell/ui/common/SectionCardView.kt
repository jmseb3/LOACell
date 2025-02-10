package com.wonddak.loacell.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SectionCardView(
    title: String? = null,
    icon: DrawableResource? = null,
    iconAction: () -> Unit = {},
    content: @Composable () -> Unit,
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
                icon?.let {
                    IconButton(
                        onClick = iconAction,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(painter = painterResource(it), null, modifier = Modifier.size(18.dp))
                    }
                }
            }
            if (title != null || icon != null) {
                HorizontalDivider()
            }
            content()
        }
    }
}