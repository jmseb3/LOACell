package com.wonddak.loacell.android.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.BuildConfig
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.viewModel.LoaCellViewModel

@Composable
fun SettingView(
    db: AppDataBase,
    loaCellViewModel: LoaCellViewModel
) {
    BackHandler() {
        loaCellViewModel.showSetting = false
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LoginInfoView(db, loaCellViewModel)

        Divider()
        SectionText(title = "버그 제보 및 건의하기") {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/acD6rQ9Tja")).also {
                context.startActivity(it)
            }
        }
        Divider()
        SectionText(title = "앱 버전 : ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
    }
}

@Composable
internal fun SectionText(
    title: String,
    action: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .noRippleClickable {
                if (action != null) {
                    action()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        if (action != null) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = SharedRes.images.arrow.drawableResId),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}