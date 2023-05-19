package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import com.wonddak.loacell.getRaidText
import com.wonddak.loacell.makeGateText
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType

@Composable
fun RaidItemRow(
    raidInfo: RaidInfo,
    onClick:() -> Unit
) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .clickable { onClick() }
        ,
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
                Text(
                    text = raidInfo.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = raidInfo.getRaidText())
                Text(text = raidInfo.makeGateText())
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
    RaidItemRow(raidInfo) {}
}
