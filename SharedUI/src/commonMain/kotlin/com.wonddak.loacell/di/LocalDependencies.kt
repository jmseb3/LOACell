package com.wonddak.loacell.di

import androidx.compose.runtime.staticCompositionLocalOf
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FileHelper

val LocalConfig = staticCompositionLocalOf<Config> {
    error("Config is not provided")
}

val LocalFileHelper = staticCompositionLocalOf<FileHelper> {
    error("FileHelper is not provided")
}

val LocalLostArkApi = staticCompositionLocalOf<LostArkApi> {
    error("LostArkApi is not provided")
}
