package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.di.LocalConfig

@Composable
fun TokenEditView(
    navigationToken:(token:String) ->Unit
) {
    val config = LocalConfig.current
    val token by config.tokenKey.collectAsState(null)
    Column(
        modifier = Modifier.fillMaxSize().padding(5.dp)
    ) {
        val tokenText = token ?: "등록된 토큰이 없습니다."
        Text(text = tokenText)

        HorizontalDivider()

        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        uriHandler.openUri("https://developer-lostark.game.onstove.com/")
                    }
                ) {
                    Icon(Icons.Filled.Link, null)
                }
                Text("웹 사이트 이동")
            }
            var myToken by remember {
                mutableStateOf("")
            }
            TextField(
                value = myToken,
                onValueChange = {
                    myToken = it
                },
                label = {
                    Text("Token")
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            myToken = ""
                        },
                        enabled = myToken.isNotEmpty()
                    ) {
                        Icon(Icons.Filled.Clear, null)
                    }
                }
            )
            TextButton(
                onClick = {
                    navigationToken(myToken)
                },
                enabled = myToken.isNotEmpty() && token !== myToken
            ) {
                Text("등록")
            }
            HorizontalDivider()
            listOf(
                "* Token은 서버에 등록되지 않고 기기내에서 관리됩니다.",
                "* Token을 등록하면 캐릭터 검색시 원할한 검색이 가능합니다."
            ).forEach { info ->
                Text(
                    text = info,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}
