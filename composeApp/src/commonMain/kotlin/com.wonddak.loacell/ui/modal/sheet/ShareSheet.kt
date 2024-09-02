package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.noRippleClickable
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.ic_share_kakaotalk
import loacell.composeapp.generated.resources.ic_share_link
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShareSheet(
    modalStatus: ModalStatus,
    roomInfo: RoomInfo,
) {
    val uniqueId: String = roomInfo.uniqueId

    BaseSheet(
        modalStatus = modalStatus,
        title = Sheet.SHARE_SHEET.title,
        useCloseIcon = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {

                        },
                    painter = painterResource(Res.drawable.ic_share_link),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .noRippleClickable {

                        },
                    painter = painterResource(Res.drawable.ic_share_kakaotalk),
                    contentDescription = null
                )
            }
        }
    }
}