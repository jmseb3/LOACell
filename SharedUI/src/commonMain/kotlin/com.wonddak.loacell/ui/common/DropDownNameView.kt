package com.wonddak.loacell.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.noRippleClickable
import com.wonddak.loacell.di.LocalConfig
import com.wonddak.loacell.ui.rememberWebLauncher
import com.wonddak.loacell.util.Config
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.search
import org.jetbrains.compose.resources.painterResource


@Composable
fun DropDownNameView(
    name: String,
    bold: Boolean = false,
    textHorizontalAlignment: Alignment.Horizontal? = null,
    textAlign: TextAlign? = null,
    fontSize: TextUnit? = null,
    otherContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val webLauncher = rememberWebLauncher()
    val config = LocalConfig.current
    val base by config.defaultUrl.collectAsState("")
    var openMenu by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable { openMenu = true },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = textHorizontalAlignment ?: Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                fontSize = fontSize ?: TextUnit.Unspecified,
                textAlign = textAlign ?: TextAlign.Center,
            )
            otherContent?.invoke()
        }
        DropdownMenu(
            expanded = openMenu,
            onDismissRequest = {
                openMenu = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("검색")
                    }
                },
                onClick = {
                    webLauncher.launchWeb(base + name)
                    openMenu = false
                }
            )
        }
    }
}
