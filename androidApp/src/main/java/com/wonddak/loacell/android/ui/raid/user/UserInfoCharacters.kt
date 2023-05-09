package com.wonddak.loacell.android.ui.raid.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character

@Composable
fun UserInfoCharacters(
    character: Character
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
    ) {
        Text(text = "${character.name}")
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val modifier = Modifier.fillMaxWidth(0.5f)
            Text(
                text = "${character.className}",
                modifier = modifier
            )
            Text(
                text = "${character.level}",
                modifier = modifier
            )
        }
        Divider()
    }
}