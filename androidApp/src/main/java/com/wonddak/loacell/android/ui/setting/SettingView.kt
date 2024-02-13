package com.wonddak.loacell.android.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.BuildConfig
import com.wonddak.loacell.android.noRippleClickable
import com.wonddak.loacell.android.ui.common.SectionCardView
import com.wonddak.loacell.android.viewModel.LoaCellViewModel
import com.wonddak.loacell.model.DialogStatus

@Composable
fun SettingView(
    loaCellViewModel: LoaCellViewModel
) {
    BackHandler() {
        loaCellViewModel.showSetting = false
    }
    val context = LocalContext.current
    val defaultSpace by loaCellViewModel.sheetSpace.collectAsState(initial = 20f)

    var showSlider by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SectionCardView(title = "로그인 정보") {
            LoginInfoView(loaCellViewModel)
        }
        SectionText(
            title = "시트 하단 여백 조정",
            clicked = showSlider,
            action = {
                showSlider = !showSlider
            }
        ) {
            Slider(
                value = defaultSpace,
                onValueChange = {
                    loaCellViewModel.setSheetSpace(it)
                },
                valueRange = 0f..40f,
                steps = 39
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "하단 여백 크기 : $defaultSpace")
                OutlinedButton(onClick = { loaCellViewModel.showDialog(DialogStatus.TEST_SHEET) }) {
                    Text(text = "테스트")
                }
            }
        }
        SectionText(title = "버그 제보 및 건의하기") {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/acD6rQ9Tja")).also {
                context.startActivity(it)
            }
        }
        SectionText(title = "앱 버전 : ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
    }
}

@Composable
internal fun SectionText(
    title: String,
    useDivider :Boolean = true,
    action: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
        if (useDivider) {
            Divider()
        }
    }
}

@Composable
internal fun SectionText(
    title: String,
    useDivider :Boolean = true,
    clicked: Boolean,
    action: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .noRippleClickable {
                    action()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = SharedRes.images.arrow.drawableResId),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(if (clicked) 90f else 0f)
            )
        }
        if (clicked) {
            content()
        }
        if (useDivider) {
            Divider()
        }
    }
}