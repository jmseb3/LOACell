package com.wonddak.loacell.android.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.wonddak.loacell.SharedRes

val roboto = FontFamily(
    Font(SharedRes.fonts.roboto_thin.fontResourceId, FontWeight.Thin),
    Font(SharedRes.fonts.roboto_regular.fontResourceId, FontWeight.Normal),
    Font(SharedRes.fonts.roboto_medium.fontResourceId, FontWeight.Medium),
    Font(SharedRes.fonts.roboto_bold.fontResourceId, FontWeight.Bold),
    Font(SharedRes.fonts.roboto_black.fontResourceId, FontWeight.Black)
)