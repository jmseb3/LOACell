/* Hallmark · pre-emit critique: P5 H5 E5 S5 R5 V4
 * component: party roster · genre: modern-minimal · theme: existing Material 3 blue
 */
package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.assetData.Synergy
import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.getDayText
import com.wonddak.loacell.model.getRaidText
import com.wonddak.loacell.model.makeGateText
import com.wonddak.loacell.theme.LoaCellRadius
import com.wonddak.loacell.theme.LoaCellSpace
import com.wonddak.loacell.ui.common.DropDownNameView
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.add
import loacell.sharedui.generated.resources.delete
import loacell.sharedui.generated.resources.screenshot
import org.jetbrains.compose.resources.painterResource


@Composable
fun RaidPartyView(
    list: List<Character?>,
    openAction: (subIndex: Int) -> Unit,
    deleteAction: (subIndex: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = LoaCellSpace.md, vertical = LoaCellSpace.sm),
        verticalArrangement = Arrangement.spacedBy(LoaCellSpace.sm),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(LoaCellRadius.card),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(modifier = Modifier.padding(LoaCellSpace.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "파티원",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Surface(
                        shape = RoundedCornerShape(LoaCellRadius.image),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text(
                            text = "${list.count { it != null }}/${list.size}",
                            modifier = Modifier.padding(
                                horizontal = LoaCellSpace.xs,
                                vertical = LoaCellSpace.xxs,
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(LoaCellSpace.sm))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(list) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 56.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            modifier = Modifier.size(LoaCellSpace.lg),
                            shape = RoundedCornerShape(LoaCellRadius.image),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.wrapContentSize(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        if (item != null) {
                            DropDownNameView(
                                name = item.name,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = LoaCellSpace.sm),
                                bold = true,
                                textAlign = TextAlign.Start,
                                textHorizontalAlignment = Alignment.Start,
                                otherContent = {
                                    Text(
                                        text = "${item.className} · Lv. ${item.level}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                },
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = LoaCellSpace.sm),
                            ) {
                                Text(
                                    text = "빈 자리",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "파티원을 추가하세요.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
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
                                contentDescription = if (item == null) "파티원 추가" else "${item.name} 삭제",
                                modifier = Modifier.size(24.dp),
                                tint = if (item == null) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                            )
                        }
                    }
                    if (index < list.lastIndex) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(LoaCellRadius.card),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column(
                modifier = Modifier.padding(LoaCellSpace.md),
                verticalArrangement = Arrangement.spacedBy(LoaCellSpace.xs),
            ) {
                Text(text = "시너지", style = MaterialTheme.typography.titleSmall)
                Synergy.getSynergyList(list).forEach { synergy ->
                    Text(
                        text = synergy,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
expect fun rememberShareImage(): suspend (ImageBitmap?) -> Unit

@Composable
expect fun rememberSaveImageBitmap(): (ImageBitmap?, () -> Unit) -> Unit

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RaidPartySimpleView(
    raidInfo: RaidInfo,
    list: List<Character?>,
    errorMsg: (String) -> Unit,
) {
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()
    val shareImage = rememberShareImage()
    val saveImageBitmap = rememberSaveImageBitmap()

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
//                                    Text(
//                                        "${idx / 4 + 1}",
//                                        textAlign = TextAlign.Center,
//                                        fontWeight = FontWeight.Bold,
//                                        modifier = Modifier.weight(1f)
//                                    )
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
        BasicAlertDialog(
            onDismissRequest = {
                saveImage = null
            }
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = 0.dp, max = 250.dp)
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
                            scope.launch {
                                shareImage(bitmap)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Share")
                    }
                    TextButton(
                        onClick = {
                            saveImageBitmap(bitmap) {
                                saveImage = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
