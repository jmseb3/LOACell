package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.database.model.Day
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.loacell.android.ui.room.common.DropDownCharacterNameView
import com.wonddak.loacell.android.ui.theme.md_theme_light_background
import com.wonddak.loacell.android.util.FileUtil
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.ext.getDayText
import com.wonddak.loacell.ext.getRaidText
import com.wonddak.loacell.ext.makeGateText
import com.wonddak.loacell.model.Synergy
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch


@Composable
fun RaidPartyView(
    list: List<Character?>,
    baseUrl: String,
    openAction: (index: Int) -> Unit,
    deleteAction: (index: Int) -> Unit,
) {
    Column {
        Card(
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 3.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(5.dp)) {
                itemsIndexed(list) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            if (item != null) {
                                DropDownCharacterNameView(
                                    base = baseUrl,
                                    name = item.name
                                )
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = item.className)
                                    Spacer(modifier = Modifier)
                                    Text(text = item.level)
                                }
                            } else {
                                Text(text = "캐릭터를 추가해주세요")
                            }
                        }
                        MyIconButton(
                            imageResource = if (item == null) SharedRes.images.add else SharedRes.images.delete
                        ) {
                            if (item == null) {
                                openAction(index)
                            } else {
                                deleteAction(index)
                            }
                        }
                    }
                }
            }
        }

        Column {
            Text(text = "시너지")
            Synergy.getSynergyList(list).forEach {
                Text(it)
            }
        }
    }
}

@OptIn(ExperimentalComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun RaidPartySimpleView(
    loaCellViewModel: LoaCellViewModel,
    raidInfo: RaidInfo,
    list: List<Character?>,
    baseUrl: String
) {
    val captureController = rememberCaptureController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(bottom = 50.dp)
            .background(md_theme_light_background)
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .capturable(captureController)
                .background(md_theme_light_background)
                .wrapContentSize()
        ) {
            Text(text = "${raidInfo.getRaidText()} ${raidInfo.makeGateText()}")
            if (raidInfo.day != Day.NONE) {
                Text(text = raidInfo.getDayText())
            }
            Card(
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 3.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 9)
                ) {
                    arrayOf(0, 4, 8, 12).forEach { idx ->
                        runCatching { list.subList(idx, idx + 4) }.getOrNull()?.let { party ->
                            item(span = {
                                GridItemSpan(1)
                            }) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.defaultMinSize(minHeight = 60.dp)
                                ) {
                                    Text(
                                        "${idx / 4 + 1}",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            items(party,span = {
                                GridItemSpan(2)
                            }) { item ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.defaultMinSize(minHeight = 60.dp)
                                ) {
                                    if (item != null) {
                                        DropDownCharacterNameView(
                                            base = baseUrl,
                                            name = item.name
                                        )
                                        Text(text = item.className)
                                        Text(text = item.level)
                                    } else {
                                        Text(text = "X")
                                    }
                                }
                            }
                            item(span = {
                                GridItemSpan(9)
                            }) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
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