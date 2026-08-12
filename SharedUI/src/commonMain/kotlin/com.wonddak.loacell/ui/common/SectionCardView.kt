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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.theme.LoaCellSpace
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
            .padding(LoaCellSpace.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = LoaCellSpace.sm)
        ) {
            Box() {
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                icon?.let {
                    IconButton(
                        onClick = iconAction,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = "섹션 동작",
                            modifier = Modifier.size(18.dp),
                        )
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
