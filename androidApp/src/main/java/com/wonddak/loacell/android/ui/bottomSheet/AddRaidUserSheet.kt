package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun AddRaidUserSheet(
    roomId: String,
    loaCellViewModel: LoaCellViewModel,
    db: AppDataBase,
    successAction: () -> Unit
) {
    BaseSheet(title = "캐릭터 정보 추가") {
        val focusList = loaCellViewModel.raidFocusList.filter { it.isNotEmpty() }

        val allUserList by db.userInfoQueriesHelper.getUsersByRoomIdFilterCharacter(
            roomId,
            focusList
        ).collectAsState(initial = emptyList())

        allUserList.map { it.name }.forEach { 
            Text(text = it)
        }

    }
}