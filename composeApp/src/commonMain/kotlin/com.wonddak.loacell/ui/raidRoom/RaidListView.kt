package com.wonddak.loacell.ui.raidRoom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.getImg
import com.wonddak.loacell.model.getRaidText
import com.wonddak.loacell.model.makeGateText
import com.wonddak.loacell.store.CommonRaidHelper
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.task_finish_done
import loacell.composeapp.generated.resources.task_finish_not
import org.jetbrains.compose.resources.painterResource


@Composable
fun RaidListView(
    raidList: List<RaidInfo>,
    navigation: (RaidInfo) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(raidList) { raidInfo ->
            RaidItemRow(Modifier.padding(vertical = 5.dp, horizontal = 5.dp), raidInfo) {
                navigation(raidInfo)
            }
        }
    }
}

@Composable
fun RaidItemRow(
    modifier: Modifier = Modifier,
    raidInfo: RaidInfo,
    onClick: () -> Unit,
) {
    val size = 100.dp
    val rShape = RoundedCornerShape(10.dp)
    Card(
        modifier = modifier
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
                        painter = painterResource(imgSrc),
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
                Res.drawable.task_finish_done
            } else {
                Res.drawable.task_finish_not
            }
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd).size(30.dp),
                onClick = {
                    CommonRaidHelper.updateFinish(
                        raidInfo.roomId,
                        raidInfo.raidId,
                        !raidInfo.isFinish
                    )
                }
            ) {
                Icon(
                    painter = painterResource(icon),
                    null
                )
            }
        }
    }

}
