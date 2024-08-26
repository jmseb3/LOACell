package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wonddak.loacell.Const
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.ui.main.LoaCellTopAppBar

@Composable
fun RaidDetailView(
    raidInfo: RaidInfo?,
    onBack: () -> Unit,
) {
    if (raidInfo == null) {
        TextButton(onClick = onBack) {
            Text("현재 접근 하려는 페이지는 삭제되었거나\n정상적인 접근이 아닙니다.")
        }
    } else {
        Scaffold(
            topBar = {
                LoaCellTopAppBar(
                    raidInfo.title,
                    onBack = onBack
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                Text(Const.NAV_RAID_DETAIL)
                Text(raidInfo.toString())
            }
        }
    }
}