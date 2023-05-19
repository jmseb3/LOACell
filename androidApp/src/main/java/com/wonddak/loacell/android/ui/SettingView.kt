package com.wonddak.loacell.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wonddak.loacell.android.ui.login.LoginInfoView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun SettingView(
    loaCellViewModel: LoaCellViewModel
) {
    BackHandler() {
        loaCellViewModel.showSetting = false
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LoginInfoView(loaCellViewModel)
    }
}