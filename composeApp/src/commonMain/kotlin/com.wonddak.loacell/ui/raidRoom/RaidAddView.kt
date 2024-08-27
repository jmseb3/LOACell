package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.model.Level
import com.wonddak.loacell.model.RaidData
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.ui.common.CheckBoxRow
import com.wonddak.loacell.ui.common.LengthLimitTextField
import com.wonddak.loacell.ui.main.LoaCellTopAppBar
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaidAddView(
    onBack: () -> Unit,
) {
    var raidInfo: RaidInfo by remember {
        mutableStateOf(
            RaidInfo()
        )
    }
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
        mutableStateOf(data[typeList[0]]?.first())
    }
    var expandLevel by remember {
        mutableStateOf(false)
    }
    var selectedLevel: Level? by remember {
        mutableStateOf(data[typeList[0]]?.first()?.level?.first())
    }
    Scaffold(
        topBar = {
            LoaCellTopAppBar(
                "레이드 추가",
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
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
                                expandType = false
                            }
                        )
                    }
                }

                data[selectedType]?.let { raidData: List<RaidData> ->
                    DropDownTextField(
                        modifier = textFieldModifier,
                        value = selectedRaid?.name?.let { Translate.getTranslate(it) } ?: "error",
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
                            }
                        }
                    }
                }
            }

            selectedLevel?.let { level ->
                if (level.differentPerGate) {
                    var gateStart by remember { mutableIntStateOf(1) }
                    var gateEnd by remember { mutableIntStateOf(1) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                    ) {
                        RaidSheetHeaderText(text = "관문 선택")
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
                        RaidSheetHeaderText(text = "입장 레벨")
                        if (level.differentPerGate) {
                            Text(text = "${level.info[raidInfo.endGateNumber - 1]}")
                        } else {
                            Text(text = "${level.info[0]}")
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        RaidSheetHeaderText(text = "입장 인원")
                        Text(text = "${level.enterPerson}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropDownTextField(
    modifier: Modifier,
    value: String,
    expand: Boolean,
    updateExpand: (Boolean) -> Unit,
    dropDownContent: @Composable () -> Unit,
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expand,
        onExpandedChange = {
            updateExpand(!expand)
        },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(horizontal = 10.dp),
            value = value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = "레이드 선택")
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand) })

        ExposedDropdownMenu(
            expanded = expand,
            onDismissRequest = { updateExpand(false) },
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            dropDownContent()
        }
    }
}

@Composable
fun RaidSheetHeaderText(text: String) {
    Column() {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun RadioItem(
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