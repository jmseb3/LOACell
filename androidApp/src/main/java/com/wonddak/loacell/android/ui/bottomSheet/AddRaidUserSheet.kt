package com.wonddak.loacell.android.ui.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRaidUserSheet(
    roomId: String,
    loaCellViewModel: LoaCellViewModel,
    db: AppDataBase,
    onDismissRequest: () -> Unit,
    successAction: () -> Unit
) {

    val focusList = loaCellViewModel.raidFocusList.filter { it.isNotEmpty() }

    val allUserList by db.userInfoQueriesHelper.getUsersByRoomIdFilterCharacter(
        roomId,
        focusList
    ).collectAsState(initial = emptyList())
    var selectedUser : UserInfo? by remember { mutableStateOf(null) }

    BaseSheet(
        title = "캐릭터 정보 추가",
        onDismissRequest = onDismissRequest,
        buttonClickAction = {}
    ) {


        var expanded by remember { mutableStateOf(false) }
        Column() {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .padding(horizontal = 10.dp),
                    value = selectedUser?.name ?:"",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "사용자 선택")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )

                ExposedDropdownMenu(
                    modifier = Modifier.background(Color.White),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    allUserList.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier.background(Color.White),
                            text = {
                                Text(
                                    text = item.name,
                                    fontWeight = if (selectedUser == item) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedUser = item
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}