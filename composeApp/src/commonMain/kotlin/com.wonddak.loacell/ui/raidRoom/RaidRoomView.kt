package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wonddak.loacell.viewModel.RaidStoreViewModel
import org.koin.compose.koinInject

@Composable
fun RaidRoomView(
    modifier: Modifier,
    roomId: String?,
    raidStoreViewModel: RaidStoreViewModel = koinInject(),
) {
    LaunchedEffect(true) {
        raidStoreViewModel.startObserveRaidInfoLust(roomId)
    }
    Column(
        modifier = modifier.fillMaxSize()
            .background(Color.White)
    ) {
        if (roomId == null) {
            Text("Error")
        } else {
            LazyColumn {
                items(raidStoreViewModel.raidList) {
                    Text(it.toString())
                }
            }
        }
    }
}