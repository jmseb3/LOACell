package com.wonddak.loacell

import androidx.activity.ComponentActivity
import androidx.compose.runtime.compositionLocalOf

val LocalActivity = compositionLocalOf<ComponentActivity> { error("CompositionLocal LocalActivity not present") }
