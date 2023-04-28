package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.database.const.RaidType

@Composable
fun AddRaidSheet() {
    BaseSheet(title = "Raid Info") {
        Column() {
            LazyRow() {
                items(RaidType.values().map { it.toKorString() }) {
                    Text(
                        modifier = Modifier.border(2.dp, Color.Gray,RoundedCornerShape(3.dp)),
                        text = it)
                    Divider()
                }
            }
        }

    }
}

@Preview
@Composable
fun AddRaidSheetPreview() {
    AddRaidSheet()
}