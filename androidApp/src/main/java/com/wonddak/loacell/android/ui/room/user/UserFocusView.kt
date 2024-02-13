package com.wonddak.loacell.android.ui.room.user

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

//유저를 선택했을때 보여질 화면
@Composable
fun UserFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val userInfo: UserInfo? = totalRoomInfo.userInfo
    val characterList  = totalRoomInfo.characterList
    val showLoading by loaCellViewModel.showLoading.collectAsState()
    val msg by loaCellViewModel.msg.collectAsState()

    userInfo?.let { userInfo ->
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
                UserInfoCharacters(userInfo, characterList)
            }
            if (showLoading) {
                BackHandler() {

                }
                LoadingView(msg)
            }
        }
    }
}