package com.wonddak.loacell.android.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.wonddak.loacell.SharedRes

val roboto = FontFamily(
    Font(SharedRes.fonts.Roboto.thin.fontResourceId, FontWeight.Thin),
    Font(SharedRes.fonts.Roboto.regular.fontResourceId, FontWeight.Normal),
    Font(SharedRes.fonts.Roboto.medium.fontResourceId, FontWeight.Medium),
    Font(SharedRes.fonts.Roboto.bold.fontResourceId, FontWeight.Bold),
    Font(SharedRes.fonts.Roboto.black.fontResourceId, FontWeight.Black)
)