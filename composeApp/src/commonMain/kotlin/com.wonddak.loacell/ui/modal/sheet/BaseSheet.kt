package com.wonddak.loacell.ui.modal.sheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.ModalStatus
import com.wonddak.loacell.SetBackAction
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSheet(
    modalStatus: ModalStatus,
    title: String,
    useCloseIcon: Boolean = true,
    content: @Composable () -> Unit,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    SetBackAction(modalStatus.status) {
        modalStatus.hide()
    }
    if (modalStatus.status) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        ModalBottomSheet(
            onDismissRequest = {
                modalStatus.hide()
            },
            sheetState = sheetState,
            dragHandle = null,
            contentWindowInsets = {
                WindowInsets.ime.only(WindowInsetsSides.Bottom)
            },
        ) {
            Column(
                Modifier.padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = if (isImeVisible) 0.dp else bottomPadding
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (useCloseIcon) {
                        IconButton(
                            onClick = { modalStatus.hide() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                null
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                content()
            }
        }
    }
}

@Composable
fun BaseSheet(
    modalStatus: ModalStatus,
    title: String,
    useCloseIcon: Boolean = true,
    errorMsg: String = "",
    enabledButton: Boolean = true,
    buttonText: String = "추가",
    updateErrorMsg: (msg: String) -> Unit = {},
    buttonClickAction: () -> Unit,
    content: @Composable () -> Unit,
) {
    BaseSheet(
        modalStatus, title, useCloseIcon
    ) {
        Column() {
            content()
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(visible = errorMsg.isNotEmpty()) {
                LaunchedEffect(errorMsg) {
                    if (errorMsg.isNotEmpty()) {
                        delay(2_000L)
                        updateErrorMsg("")
                    }
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMsg,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }
            OutlinedButton(
                onClick = {
                    buttonClickAction()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabledButton
            ) {
                Text(text = buttonText)
            }
        }
    }
}