package com.wonddak.loacell.android.ui.room.raid

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.database.model.Day
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.util.FileUtil
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomState
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun RaidFocusView(
    loaCellViewModel: LoaCellViewModel
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val totalRoomInfo by loaCellViewModel.totalRoomInfo.collectAsState()
    val baseUrl by loaCellViewModel.defaultUrl.collectAsState(initial = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(md_theme_light_background)
            .noRippleClickable() { },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BackHandler() {
            loaCellViewModel.clearFocusItem()
        }
        totalRoomInfo.raidInfo?.let { raidInfo ->
            Column(
                modifier = Modifier
                    .capturable(captureController)
                    .fillMaxWidth()
                    .background(md_theme_light_background)
                    .wrapContentSize()
            ) {
                Column {
                    Text(text = "${raidInfo.getRaidText()} ${raidInfo.makeGateText()}")
                    if (raidInfo.day != Day.NONE) {
                        Text(text = raidInfo.getDayText())
                    }
                }
                loaCellViewModel.apply {
                    RaidPartyView(
                        totalRoomInfo.partyCharacterList,
                        baseUrl = baseUrl,
                        openAction = { index ->
                            val userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                            if (userAndCharacterMap.isEmpty()) {
                                showSnackBar(
                                    message = "추가 가능한 인원이 없습니다.",
                                    label = "이동",
                                ) {
                                    clearFocusItem()
                                    setTabStatus(RoomState.User)
                                    showDialog(DialogStatus.USER_ADD)
                                }
                            } else {
                                updatePartyFocusIndex(index)
                                showDialog(DialogStatus.RAID_USER_ADD)
                            }
                        },
                        deleteAction = { index ->
                            updatePartyFocusIndex(index)
                            showDialog(DialogStatus.RAID_USER_DELETE)
                        }
                    )
                }
                Spacer(modifier = Modifier)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    scope.launch {
                        val bitmapAsync = captureController.captureAsync()
                        try {
                            val bitmap = bitmapAsync.await()
                            FileUtil.requestShare(
                                context,
                                raidInfo.roomId,
                                raidInfo.raidId,
                                bitmap.asAndroidBitmap()
                            )
                        } catch (error: Throwable) {
                            error.printStackTrace()
                            loaCellViewModel.showSnackBar("이미지 생성에 실패했습니다.")
                        }
                    }
                }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = SharedRes.images.screenshot.drawableResId),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "공격대 공유")
                    }
                }
            }
        }


    }
}