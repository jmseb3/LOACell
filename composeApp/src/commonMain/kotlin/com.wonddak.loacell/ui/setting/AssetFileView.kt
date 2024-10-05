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
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.util.FileUtil
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


@Composable
fun AssetFileView(
    totalFileName : List<String>
) {
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
                val (name,ext) = file.split(".")
                Text(name)
            }
        }
    }
}

@Preview
@Composable
fun AssetFilePreview() {
    AssetFileView(
        listOf(
            "raid_1.json",
            "translate_1.json",
            "synergy_1.json"
        )
    )
}