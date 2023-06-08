package com.wonddak.loacell.android.ui.room.raid

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import com.wonddak.database.ext.getRaidText
import com.wonddak.database.ext.makeGateText
import com.wonddak.loacell.ext.getImg
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
import com.wonddak.loacell.store.CommonRaidHelper

@Composable
fun RaidItemRow(
    raidInfo: RaidInfo,
    onClick: () -> Unit
) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
            .clickable { onClick() },
        shape = rShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(size)
            ) {
                val imgSrc = raidInfo.getImg()
                if (imgSrc != null) {
                    Image(
                        painter = painterResource(id = imgSrc.drawableResId),
                        contentDescription = null,
                        Modifier
                            .size(size)
                            .clip(rShape)
                    )
                } else {
                    Image(
                        bitmap = ImageBitmap(100, 100),
                        contentDescription = null,
                        Modifier
                            .size(size)
                            .clip(rShape)
                    )

                }

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
            val icon = if (raidInfo.isFinish) {
                SharedRes.images.task_finish_done
            } else {
                SharedRes.images.task_finish_not
            }
            MyIconButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                imageResource = icon
            ) {
                CommonRaidHelper.updateFinish(raidInfo.roomId,raidInfo.raidId,!raidInfo.isFinish)
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
        type = RaidType.ABRELSHUD,
        Difficulty = Difficulty.Normal,
        startGateNumber = 1L,
        endGateNumber = 3L,
        isFinish = false,
        party1characterList = emptyList(),
        party2characterList = emptyList()
    )
    RaidItemRow(raidInfo) {}
}
