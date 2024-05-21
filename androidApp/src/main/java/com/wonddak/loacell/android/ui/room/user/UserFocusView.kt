package com.wonddak.loacell.android.ui.room.user

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

//유저를 선택했을때 보여질 화면
@Composable
fun UserFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo = loaCellViewModel.totalRoomInfoValue
    val characterList  = totalRoomInfo.characterList
    val showLoading = loaCellViewModel.showLoading
    val msg = loaCellViewModel.msg
    val baseUrl = loaCellViewModel.defaultUrl

    totalRoomInfo.userInfo?.let { userInfo ->
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(md_theme_light_background)
                    .noRippleClickable()
            ) {
                BackHandler() {
                    loaCellViewModel.clearFocusItem()
                }
                UserInfoCharacters(userInfo, characterList,baseUrl)
            }
            if (showLoading) {
                BackHandler() {

                }
                LoadingView(msg)
            }
        }
    }
}