@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsApi::class,
    ExperimentalSettingsApi::class
)

package com.wonddak.loacell

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings

expect class Config {
    val settings :FlowSettings
}

suspend fun Config.clear() = this.settings.clear()
fun Config.getLongFlow(key:String, defaultValue:Long=0L) = this.settings.getLongFlow(key,defaultValue)
suspend fun Config.getLong(key:String, defaultValue:Long=0L) = this.settings.getLong(key,defaultValue)
suspend fun Config.putLong(key:String, value:Long) = this.settings.putLong(key,value)


object ConfigKeys {
    const val HomeRefreshKey = "home_refresh"
}