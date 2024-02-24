package com.wonddak.loacell.android.ui.room.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.toPainter
import com.wonddak.loacell.util.openName

@Composable
fun DropDownCharacterNameView(
    modifier: Modifier = Modifier,
    name: String,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val context = LocalContext.current
    var openMenu by remember {
        mutableStateOf(false)
    }
    Text(
        modifier = modifier.noRippleClickable { openMenu = true },
        text = name,
        fontWeight = fontWeight
    )
    DropdownMenu(expanded = openMenu, onDismissRequest = { openMenu = false }) {
        DropdownMenuItem(
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = SharedRes.images.search.toPainter(),
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("검색")
                }
            },
            onClick = {
                context.openName(name)
                openMenu = false
            }
        )
    }
}
