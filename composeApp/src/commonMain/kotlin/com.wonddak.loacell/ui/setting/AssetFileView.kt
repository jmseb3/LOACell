package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.viewModel.SplashViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AssetFileView(
    splashViewModel: SplashViewModel
) {
    val totalFileName = splashViewModel.totalFileName
    println(">>>[2] = SP $splashViewModel")
    val fileHelper: FileHelper = koinInject()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Asset File Info",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        totalFileName.forEach { file ->
            Row(
                modifier = Modifier.padding(3.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val (name, ext) = file.split(".")
                Text(name)
                TextButton(
                    onClick = {
                        fileHelper.deleteAssetFile(file)
                    },
                    enabled = fileHelper.isExistAsset(file)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
