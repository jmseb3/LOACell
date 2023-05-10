package com.wonddak.loacell.android.ui.bottomSheet

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonColors
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.RangeSlider
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddRaidSheet() {
    BaseSheet(title = "레이드 정보 추가") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var expanded by remember { mutableStateOf(false) }
            var nowType : RaidType by remember {
                mutableStateOf(RaidType.ETC)
            }
            var nowDifficulty : Difficulty by remember {
                mutableStateOf(Difficulty.Normal)
            }
            val radioOptions = Difficulty.values()

            Spacer(modifier = Modifier.height(10.dp))

            //타입
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    value = nowType.toKorString(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "레이드 정보 선택")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    RaidType.values().forEach { item ->
                        DropdownMenuItem(
                            content = {
                                Text(
                                    text = item.toKorString(),
                                    fontWeight = if (nowType == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                nowType = item
                                if (!nowType.accessibleDifficulty().contains(nowDifficulty)) {
                                    nowDifficulty = Difficulty.Normal
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }

            //난이도
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                radioOptions.forEach { difficulty ->
                    val selected = difficulty == nowDifficulty
                    val enabled = nowType.accessibleDifficulty().contains(difficulty)

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = selected,
                                onClick = {
                                    nowDifficulty = difficulty
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            enabled = enabled,
                            onClick = {nowDifficulty = difficulty },
                            colors = RadioButtonDefaults.colors(
                                //TODO Match Theme Color
                                selectedColor = Color(0xFF6200EE)
                            )
                        )
                        Text(
                            text = difficulty.toKorString(),
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .fillMaxWidth(),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (enabled) Color.Black else Color.Gray
                        )
                    }
                }
            }
            var sliderValues by remember {
                mutableStateOf(1f..3f)
            }
            AnimatedVisibility(visible = nowType == RaidType.ABRELSHUD) {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "관문 선택")
                    RangeSlider(
                        value = sliderValues,
                        onValueChange = { sliderValues_ ->
                            sliderValues = sliderValues_
                        },
                        valueRange = 1f..3f,
                        onValueChangeFinished = {
                            Log.d(
                                "JWH",
                                "First: ${sliderValues.start}, Last: ${sliderValues.endInclusive}"
                            )
                        },
                        steps = 1
                    )
                }
            }

        }

    }
}

@Preview
@Composable
fun AddRaidSheetPreview() {
    AddRaidSheet()
}