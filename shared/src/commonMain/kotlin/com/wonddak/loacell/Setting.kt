@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsApi::class,
    ExperimentalSettingsApi::class
)

package com.wonddak.loacell

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.datetime.Clock

expect class Config {
    val settings :FlowSettings
}

suspend fun Config.clear() = this.settings.clear()
fun Config.getLongFlow(key:String, defaultValue:Long=0L) = this.settings.getLongFlow(key,defaultValue)
suspend fun Config.getLong(key:String, defaultValue:Long=0L) = this.settings.getLong(key,defaultValue)
suspend fun Config.putLong(key:String, value:Long) = this.settings.putLong(key,value)

suspend fun Config.syncStart(
    force:Boolean,
    syncResult :(result:Boolean) -> Unit
) {
    val nowTime = Clock.System.now().toEpochMilliseconds()
    if (force) {
        this.putLong(ConfigKeys.HomeRefreshKey,nowTime)
        syncResult(true)
        return
    }
    val syncTime = this.getLong(ConfigKeys.HomeRefreshKey)
    if (nowTime - syncTime > 60 * 5 * 1000) {
        this.putLong(ConfigKeys.HomeRefreshKey,nowTime)
        syncResult(true)
    } else {
        syncResult(false)
    }
}

object ConfigKeys {
    const val HomeRefreshKey = "home_refresh"
}