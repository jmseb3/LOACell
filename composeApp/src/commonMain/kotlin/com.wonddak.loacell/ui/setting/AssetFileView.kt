package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.viewModel.SplashViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AssetFileView(
    splashViewModel: SplashViewModel
) {
    val totalFileName = splashViewModel.totalFileName
    val fileHelper: FileHelper = koinInject()
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = "Asset 파일 정보",
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        Row(
            modifier = Modifier.padding(vertical = 3.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "파일명",
                modifier = Modifier.weight(1f)
            )
            Text(
                "버전",
                modifier = Modifier.weight(1f)
            )
            Text(
                "",
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))
        totalFileName.forEach { file ->
            Row(
                modifier = Modifier.padding(vertical = 3.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (info, ext) = file.split(".")
                val (name, version) = info.split("_")
                Text(
                    name,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    version,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = {
                        fileHelper.deleteAssetFile(file)
                    },
                    enabled = fileHelper.isExistAsset(file),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("파일 삭제")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 10.dp))
        }
    }
}
