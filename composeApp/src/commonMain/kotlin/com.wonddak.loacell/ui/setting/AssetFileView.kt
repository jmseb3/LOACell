package com.wonddak.loacell.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.viewModel.SplashViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AssetFileView(
    splashViewModel: SplashViewModel
) {
    val totalFileName = splashViewModel.totalFileName
    println(">>>[2] = SP $splashViewModel")

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
                modifier = Modifier.padding(3.dp)
            ) {
                val (name, ext) = file.split(".")
                Text(name)
            }
        }
    }
}
