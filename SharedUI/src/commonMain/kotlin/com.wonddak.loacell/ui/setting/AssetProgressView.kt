package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.di.LocalAssetStorage
import com.wonddak.loacell.util.FileHelper
import kotlinx.coroutines.delay

@Composable
fun AssetProgressView(
    name: String,
    fileHelper: FileHelper,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val assetStorage = LocalAssetStorage.current
    Column(
        modifier = Modifier
            .size(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var title by remember {
            mutableStateOf("")
        }
        LaunchedEffect(true) {
            title = "파일 삭제 요청"
            delay(1000)
            title = "파일 삭제중"
            val result = fileHelper.deleteAssetFile(name)
            delay(1000)
            if (result) {
                title = "다운로드 요청중"
                delay(1000L)
                assetStorage.downloadAssetFile(
                    fileName = name,
                    fileHelper = fileHelper,
                    successAction = {
                        title = "다운로드 성공"
                        onSuccess()
                    },
                    failAction = {
                        title = "다운로드 실패"
                    }
                )
            } else {
                title = "파일 삭제에 실패 했습니다."
                delay(1000)
                onBack()
            }
        }

        LaunchedEffect(title) {
            if (title == "다운로드 성공" || title == "다운로드 실패") {
                delay(1000L)
                onBack()
            }
        }

        CircularProgressIndicator()
        Text(title)
    }
}
