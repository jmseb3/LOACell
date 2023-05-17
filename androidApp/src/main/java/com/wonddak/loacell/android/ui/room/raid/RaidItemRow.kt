package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.android.R
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType

@Composable
fun RaidItemRow(raidInfo: RaidInfo) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(size),
        shape = rShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
        ) {
            Image(
                painter = painterResource(id = R.drawable.valtan),
                contentDescription = null,
                Modifier
                    .size(size)
                    .clip(rShape)
            )
            Column(
                modifier = Modifier.padding(5.dp)
            ) {
                val headerText =
                    "${raidInfo.type!!.toKorString()} - ${raidInfo.Difficulty!!.toKorString()}"
                Text(
                    text = raidInfo.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = headerText)
            }
        }
    }

}


@Composable
@Preview
fun RaidItemRowPreview() {
    val raidInfo = RaidInfo(
        raidId = "",
        roomId = "",
        title = "test",
        type = RaidType.VALTAN,
        Difficulty = Difficulty.Normal,
        startGateNumber = 1L,
        endGateNumber = 3L,
        isFinish = false
    )
    RaidItemRow(raidInfo)
}
