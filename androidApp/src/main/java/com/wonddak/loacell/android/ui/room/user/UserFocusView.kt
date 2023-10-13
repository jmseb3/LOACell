package com.wonddak.loacell.android.ui.room.user

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.LoadingView
import com.wonddak.loacell.android.ui.dialog.DeleteCharacterDialog
import com.wonddak.loacell.android.ui.dialog.EditCharacterDialog
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.sharedapi.lostark.LostArkApi
import com.wonddak.sharedapi.onFail
import com.wonddak.sharedapi.onFailOnlyMsg
import com.wonddak.sharedapi.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//유저를 선택했을때 보여질 화면
@Composable
fun UserFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val roomId = totalRoomInfo.roomInfo?.uniqueId ?: ""
    val userInfo: UserInfo? by loaCellViewModel.userInfo.collectAsState(null)
    val characterList by loaCellViewModel.characterList.collectAsState()
    val dialogStatus by loaCellViewModel.dialogStatus.collectAsState()

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
                loaCellViewModel.apply {
                    UserInfoCharacters(userInfo,characterList)
                    if (dialogStatus == DialogStatus.CHARACTER_EDIT) {
                        EditCharacterDialog(
                            userInfo = userInfo,
                            characterList = characterList ,
                            confirm = { name ->
                                CommonUserHelper.updateRepresentativeCharacter(
                                    roomId,
                                    userInfo.name,
                                    name
                                )
                                hideDialog()
                            },
                            dismiss = {
                                hideDialog()
                            }
                        )
                    } else if (dialogStatus == DialogStatus.CHARACTER_DELETE) {
                        DeleteCharacterDialog(
                            name = userInfo.name,
                            confirm = {
                                CommonUserHelper.delete(
                                    roomId,
                                    userInfo.name,
                                    failAction = { e ->
                                        showSnackBar(e)
                                    },
                                    successAction = {
                                        loaCellViewModel.clearFocusItem()
                                        hideDialog()
                                    }
                                )
                            },
                            dismiss = {
                                hideDialog()
                            }
                        )
                    }
                }
            }
        }

        var msg by remember {
            mutableStateOf("캐릭터 정보를 갱신합니다.")
        }

        LaunchedEffect(loaCellViewModel.showLoading) {
            if (loaCellViewModel.showLoading) {
                val characterResult = LostArkApi().getCharacterInfo(userInfo.representativeCharacter)
                characterResult.onSuccess { list ->
                    CommonUserHelper.addOrUpdate(
                        roomId = roomId,
                        name = userInfo.name,
                        representativeCharacter = userInfo.representativeCharacter,
                        characterList = list,
                        failAction = { e ->
                            launch {
                                msg = "서버 데이터 저장에 실패했습니다."
                                delay(1_500L)
                                loaCellViewModel.showLoading = false
                            }
                        }
                    ) {
                        loaCellViewModel.showLoading = false
                    }
                }
                characterResult.onFail { code, message ->
                    delay(1_500L)
                    msg = message
                    loaCellViewModel.showLoading = false
                }
                characterResult.onFailOnlyMsg {  message ->
                    msg = message
                    loaCellViewModel.showLoading = false
                }
            }
        }
        if (loaCellViewModel.showLoading) {
            BackHandler() {

            }

            LoadingView(msg)
        }
    }
}