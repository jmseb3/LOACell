package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.Character
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton

@Composable
fun RaidPartyView(
    list: List<Character?>,
    openAction: (index: Int) -> Unit,
    deleteAction: (index: Int) -> Unit,
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
    ) {
        LazyColumn(modifier = Modifier.padding(5.dp)) {
            itemsIndexed(list) { index, item ->
                val modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)

                if (index == 4) {
                    Divider()
                }
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = item?.name ?: "캐릭터를 추가해주세요")
                        if (item != null) {
                            Row(
                                modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = item.className)
                                Text(text = item.level)
                            }
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
}