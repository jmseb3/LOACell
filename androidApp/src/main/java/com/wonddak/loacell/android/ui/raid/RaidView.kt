package com.wonddak.loacell.android.ui.raid

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RaidView() {

}

@Preview
@Composable
fun RaidViewPreview() {
    RaidView()
}

@Composable
fun RaidViewListItem(
    name: String,
    description: String
) {
    Column() {
        Text(text = name)
        Text(text = description)
    }
}