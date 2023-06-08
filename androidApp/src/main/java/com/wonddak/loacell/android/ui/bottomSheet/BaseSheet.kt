package com.wonddak.loacell.android.ui.bottomSheet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.android.ui.common.MyIconButton
import kotlinx.coroutines.delay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSheet(
    title: String,
    useCloseIcon : Boolean = false,
    onDismissRequest :() -> Unit = {},
    content: @Composable () -> Unit
) {
    val sheetState :SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    BackHandler() {
        onDismissRequest()
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            Box() {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (useCloseIcon) {
                    MyIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        imageResource = SharedRes.images.close
                    ) {
                        onDismissRequest()
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Divider()
            content()
        }
    }
}
@Composable
fun BaseSheet(
    title: String,
    errorMsg :String = "",
    useCloseIcon : Boolean = false,
    enabledButton : Boolean = true,
    buttonText :String = "추가",
    updateErrorMsg :(msg:String) -> Unit ={},
    onDismissRequest :() -> Unit = {},
    buttonClickAction : () -> Unit,
    content: @Composable () -> Unit
) {
    BaseSheet(
        title,useCloseIcon,onDismissRequest
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
            Spacer(modifier = Modifier.height(10.dp))
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