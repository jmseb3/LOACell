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
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.koinInject

@Composable
fun RaidRoomView(
    modifier: Modifier,
    roomId: String?,
    roomViewModel: StoreViewModel = koinInject(),
    raidStoreViewModel: RaidViewModel = koinInject(),
) {
    LaunchedEffect(true) {
        raidStoreViewModel.startObserveRaidInfoList(roomId)
    }
    val roomInfo = roomViewModel.findRoomInfo(roomId)
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