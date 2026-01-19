package com.wonddak.loacell.ui.raidRoom.raid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.model.Day
import com.wonddak.loacell.model.Level
import com.wonddak.loacell.model.RaidData
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.ui.common.CheckBoxRow
import com.wonddak.loacell.ui.common.DropDownTextField
import com.wonddak.loacell.ui.common.LengthLimitTextField
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import com.wonddak.loacell.ui.modal.dialog.TimePickerDialog
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.schedule
import org.jetbrains.compose.resources.painterResource
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidAddView(
    roomId: String,
    prevData: RaidInfo? = null,
    onBack: () -> Unit,
) {
    val textFieldModifier = Modifier.fillMaxWidth()
    val data = RaidItem.getMapData()
    val typeList = data.keys.toList()

    var expandType by remember {
        mutableStateOf(false)
    }
    var selectedType by remember {
        mutableStateOf(typeList[0])
    }
    var expandRaid by remember {
        mutableStateOf(false)
    }
    var selectedRaid: RaidData? by remember {
        mutableStateOf(prevData?.raidItem ?: data[typeList[0]]?.first())
    }
    var selectedLevel: Level? by remember {
        mutableStateOf(prevData?.level ?: data[typeList[0]]?.first()?.level?.first())
    }

    var raidInfo: RaidInfo by remember {
        mutableStateOf(
            prevData ?: RaidInfo(
                roomId,
                selectedRaid?.name ?: "",
                selectedLevel?.difficulty ?: ""
            )
        )
    }

    var showDayUse by remember {
        mutableStateOf(prevData?.let { it.day != Day.NONE } ?: false)
    }
    var showPicker by remember {
        mutableStateOf(false)
    }
    val timePickerState = rememberTimePickerState(
        is24Hour = false,
        initialHour = prevData?.hour ?: 0,
        initialMinute = prevData?.minute ?: 0
    )
    Scaffold(
        topBar = {
            LoaCellTopAppBar(
                if (prevData == null) "레이드 추가" else "레이드 수정",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                LengthLimitTextField(
                    modifier = textFieldModifier.padding(10.dp),
                    text = raidInfo.title,
                    label = "제목",
                    placeHolder = "제목을 입력하세요.",
                    maxLine = 1,
                    maxLength = 10,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    textChange = {
                        raidInfo = raidInfo.copy(title = it)
                    }
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DropDownTextField(
                        modifier = textFieldModifier,
                        label = "레이드 구분",
                        value = selectedType,
                        expand = expandType,
                        updateExpand = {
                            expandType = it
                        }
                    ) {
                        typeList.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        fontWeight = if (selectedType == item) FontWeight.Bold else FontWeight.Normal
                                    )
                                }, onClick = {
                                    selectedType = item
                                    selectedRaid = data[item]?.first()
                                    selectedLevel = data[item]?.first()?.level?.first()

                                    raidInfo = raidInfo.copy(
                                        type = selectedRaid?.name ?: "",
                                        difficulty = selectedLevel?.difficulty ?: ""
                                    )
                                    expandType = false
                                }
                            )
                        }
                    }

                    data[selectedType]?.let { raidData: List<RaidData> ->
                        DropDownTextField(
                            modifier = textFieldModifier,
                            label = "레이드 선택",
                            value = selectedRaid?.name?.let { Translate.getTranslate(it) }
                                ?: "error",
                            expand = expandRaid,
                            updateExpand = {
                                expandRaid = it
                            }
                        ) {
                            raidData.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = Translate.getTranslate(item.name),
                                            fontWeight = if (selectedRaid == item) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }, onClick = {
                                        selectedRaid = item
                                        selectedLevel = item.level.first()
                                        raidInfo = raidInfo.copy(
                                            type = selectedRaid?.name ?: "",
                                            difficulty = selectedLevel?.difficulty ?: ""
                                        )
                                        expandRaid = false
                                    }
                                )
                            }
                        }
                    }
                    selectedRaid?.let { raidData ->
                        Column {
                            raidData.level.forEach { level ->
                                RadioItem(
                                    selectedLevel == level,
                                    Translate.getTranslate(level.difficulty)
                                ) {
                                    selectedLevel = level
                                    raidInfo =
                                        raidInfo.copy(difficulty = selectedLevel?.difficulty ?: "")
                                }
                            }
                        }
                    }
                }

                selectedLevel?.let { level ->
                    if (level.differentPerGate) {
                        var gateStart by remember {
                            mutableIntStateOf(
                                prevData?.startGateNumber ?: 1
                            )
                        }
                        var gateEnd by remember { mutableIntStateOf(prevData?.endGateNumber ?: 1) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                        ) {
                            RaidHeaderText(text = "관문 선택")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                (1..level.info.size).forEach { idx ->
                                    CheckBoxRow(modifier = Modifier.weight(1f),
                                        text = idx.toString(),
                                        checked = idx in gateStart..gateEnd,
                                        enabled = true,
                                        onClick = { value ->
                                            if (value) {
                                                gateStart = min(gateStart, idx)
                                                gateEnd = gateEnd.coerceAtLeast(idx)
                                            } else {
                                                if (gateStart == idx) {
                                                    gateStart = idx + 1
                                                } else if (gateEnd == idx) {
                                                    gateEnd = idx - 1
                                                }
                                                if (gateEnd < gateStart) {
                                                    gateStart = 1
                                                    gateEnd = 1
                                                }
                                            }
                                            raidInfo = raidInfo.copy(
                                                startGateNumber = gateStart,
                                                endGateNumber = gateEnd
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            RaidHeaderText(text = "입장 레벨")
                            Text(text = raidInfo.getMinLevel().toString())
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            RaidHeaderText(text = "입장 인원")
                            Text(text = "${level.enterPerson}")
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CheckBoxRow(modifier = Modifier,
                        text = "일정 지정",
                        checked = showDayUse,
                        enabled = true,
                        onClick = {
                            showDayUse = it
                            if (!showDayUse) {
                                raidInfo =
                                    raidInfo.copy(dayIndex = -1, hour = 0, minute = 0)
                            }
                        })
                    Spacer(modifier = Modifier.weight(1f))
                }
                AnimatedVisibility(showDayUse) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Day.MON.getList().forEach {
                                DayButton(
                                    it,
                                    Modifier.weight(1f),
                                    raidInfo.day == it
                                ) {
                                    raidInfo = raidInfo.copy(dayIndex = it.index.toLong())
                                }
                            }
                        }
                        Button(onClick = {
                            showPicker = true
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Text(raidInfo.getTimeText())
                                Icon(painter = painterResource(Res.drawable.schedule), null)
                            }
                        }
                        if (showPicker) {
                            TimePickerDialog(
                                state = timePickerState,
                                onCancel = {
                                    showPicker = false
                                },
                                onConfirm = {
                                    raidInfo = raidInfo.copy(
                                        hour = timePickerState.hour,
                                        minute = timePickerState.minute
                                    )
                                    showPicker = false
                                }
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = {
                    if (prevData == null) {
                        CommonRaidHelper.add(
                            raidInfo,
                            failAction = {

                            },
                            successAction = {
                                onBack()
                            }
                        )
                    } else {
                        CommonRaidHelper.update(
                            raidInfo,
                            failAction = {

                            },
                            successAction = {
                                onBack()
                            }
                        )
                    }
                },
                enabled =
                raidInfo.title.isNotEmpty() &&
                        (if (showDayUse) raidInfo.day != Day.NONE else true) &&
                        (if (prevData == null) true else (prevData != raidInfo)),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).height(50.dp),
            ) {
                Text(text = if (prevData == null) "추가" else "수정")
            }
        }
    }
}


@Composable
private fun RaidHeaderText(text: String) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RadioItem(
    selected: Boolean,
    text: String,
    click: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .noRippleClickable(click)
            .padding(end = 16.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = click
        )
        Text(text = text)
    }
}

@Composable
private fun DayButton(
    day: Day, modifier: Modifier, selected: Boolean, update: () -> Unit,
) {
    val color = if (selected) Color.Black else Color.Transparent
    TextButton(
        modifier = modifier.border(BorderStroke(1.dp, color), shape = RoundedCornerShape(8.dp)),
        onClick = update
    ) {
        Text(
            text = day.text, textAlign = TextAlign.Center, color = Color.Black
        )
    }
}