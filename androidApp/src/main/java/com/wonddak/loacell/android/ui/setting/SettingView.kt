package com.wonddak.loacell.android.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun SettingView(
    db :AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    BackHandler() {
        loaCellViewModel.showSetting = false
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LoginInfoView(db,loaCellViewModel)

        Divider()

        OutlinedButton(onClick = {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/tMt3EXER")).also {
                context.startActivity(it)
            }
        }) {
            Text(text = "버그 제보 및 건의하기")
        }
    }
}