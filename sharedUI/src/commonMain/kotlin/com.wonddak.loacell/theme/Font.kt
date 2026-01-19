package com.wonddak.loacell.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import loacell.sharedui.generated.resources.Res
import loacell.sharedui.generated.resources.Roboto_Black
import loacell.sharedui.generated.resources.Roboto_Bold
import loacell.sharedui.generated.resources.Roboto_Medium
import loacell.sharedui.generated.resources.Roboto_Regular
import loacell.sharedui.generated.resources.Roboto_Thin
import org.jetbrains.compose.resources.Font

@Composable
fun roboto() = FontFamily(
    Font(Res.font.Roboto_Thin, FontWeight.Thin,FontStyle.Normal),
    Font(Res.font.Roboto_Regular, FontWeight.Normal,FontStyle.Normal),
    Font(Res.font.Roboto_Medium, FontWeight.Medium,FontStyle.Normal),
    Font(Res.font.Roboto_Bold, FontWeight.Bold,FontStyle.Normal),
    Font(Res.font.Roboto_Black, FontWeight.Black,FontStyle.Normal),
)