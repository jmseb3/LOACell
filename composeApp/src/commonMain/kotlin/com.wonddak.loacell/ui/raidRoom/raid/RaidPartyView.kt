package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.assetData.Synergy
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.ui.common.DropDownNameView
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.add
import loacell.composeapp.generated.resources.delete
import loacell.composeapp.generated.resources.screenshot
import org.jetbrains.compose.resources.painterResource


@Composable
fun RaidPartyView(
    list: List<Character?>,
    openAction: (subIndex: Int) -> Unit,
    deleteAction: (subIndex: Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(5.dp)) {
                itemsIndexed(list) { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            if (item != null) {
                                DropDownNameView(name = item.name,
                                    textAlign = TextAlign.Start,
                                    textHorizontalAlignment = Alignment.Start,
                                    otherContent = {
                                        Row() {
                                            Text(text = item.className)
                                            Spacer(Modifier.width(10.dp))
                                            Text(text = item.getLevel().toString())
                                        }
                                    })
                            } else {
                                Text(text = "캐릭터를 추가해주세요")
                            }
                        }
                        IconButton(onClick = {
                            if (item == null) {
                                openAction(index)
                            } else {
                                deleteAction(index)
                            }
                        }) {
                            val iconRes = if (item == null) {
                                Res.drawable.add
                            } else {
                                Res.drawable.delete
                            }
                            Icon(
                                painter = painterResource(iconRes),
                                null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }

        Card(
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                Text(text = "시너지")
                Spacer(Modifier.height(10.dp))
                Synergy.getSynergyList(list).forEach {
                    Text(it)
                }
            }
        }
    }
}

expect fun shareImage(bitmap: ImageBitmap?)
expect fun saveImageBitmap(bitmap: ImageBitmap?,complete : () -> Unit)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RaidPartySimpleView(
    raidInfo: RaidInfo,
    list: List<Character?>,
    errorMsg: (String) -> Unit,
) {
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()

    var saveImage: ImageBitmap? by remember {
        mutableStateOf(null)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.capturable(captureController)) {

            Column(
                modifier = Modifier.wrapContentSize().background(Color.White),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(text = "${raidInfo.getRaidText()} ${raidInfo.makeGateText()}")
                        if (raidInfo.day != Day.NONE) {
                            Text(text = raidInfo.getDayText())
                        }
                    }
                }
                Card(
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Column {
                        arrayOf(0, 4, 8, 12).forEach { idx ->
                            runCatching { list.subList(idx, idx + 4) }.getOrNull()?.let { party ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .defaultMinSize(minHeight = 50.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${idx / 4 + 1}",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    party.forEach { item ->
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            if (item != null) {
                                                DropDownNameView(item.name,
                                                    fontSize = 12.sp,
                                                    otherContent = {
                                                        Text(
                                                            text = item.className, fontSize = 12.sp
                                                        )
                                                        Text(
                                                            text = item.getLevel().toString(),
                                                            fontSize = 12.sp
                                                        )
                                                    })
                                            } else {
                                                Text(text = "X")
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                scope.launch {
                    val bitmapAsync = captureController.captureAsync()
                    try {
                        val bitmap = bitmapAsync.await()
                        saveImage = bitmap
                    } catch (error: Throwable) {
                        errorMsg(error.message ?: "error")
                    }
                }
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.screenshot),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "공격대 공유")
                }
            }
        }
    }
    saveImage?.let { bitmap ->
        BasicAlertDialog(onDismissRequest = {
            saveImage = null
        }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = 0.dp, max=250.dp)
                        .padding(10.dp)
                ) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            shareImage(bitmap)
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text("Share")
                    }
                    TextButton(
                        onClick = {
                            saveImageBitmap(bitmap) {
                                saveImage = null
                            }
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}