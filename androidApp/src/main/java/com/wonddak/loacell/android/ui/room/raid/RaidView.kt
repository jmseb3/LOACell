package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.database.AppDataBase

@Composable
fun RaidView(
    db: AppDataBase,
    roomId: String,
    loaCellViewModel: LoaCellViewModel
) {

    val raidInfoList by db.raidInfoQueriesHelper.getALlByRoomId(roomId)
        .collectAsState(initial = emptyList())
    Column() {
        LazyColumn(
            modifier = Modifier.padding(10.dp)
        ) {
            items(raidInfoList) { raidInfo ->
                RaidItemRow(raidInfo)
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}