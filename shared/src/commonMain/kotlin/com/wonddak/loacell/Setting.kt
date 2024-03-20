@file:OptIn(ExperimentalSettingsApi::class)

package com.wonddak.loacell

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings

expect class Config {
    val settings: FlowSettings
}
suspend fun Config.clear() = this.settings.clear()
fun Config.getLongFlow(key: String, defaultValue: Long = 0L) =
    this.settings.getLongFlow(key, defaultValue)

suspend fun Config.getLong(key: String, defaultValue: Long = 0L) =
    this.settings.getLong(key, defaultValue)

suspend fun Config.putLong(key: String, value: Long) = this.settings.putLong(key, value)

fun Config.getFloatFlow(key: String, defaultValue: Float = 0f) =
    this.settings.getFloatFlow(key, defaultValue).toCommonFlow()

suspend fun Config.getFloat(key: String, defaultValue: Float = 0f) =
    this.settings.getFloat(key, defaultValue)

suspend fun Config.putFloat(key: String, value: Float) = this.settings.putFloat(key, value)


suspend fun Config.getString(key: String, defaultValue: String = "") =
    this.settings.getString(key, defaultValue)

fun Config.getStringFlow(key: String, defaultValue: String = "") =
    this.settings.getStringFlow(key, defaultValue).toCommonFlow()


suspend fun Config.putSting(key: String,value:String) = this.settings.putString(key, value)

object ConfigKeys {
    const val HomeRefreshKey = "home_refresh"
    const val SheetSpace = "sheet_space"
    const val DefaultUrl = "default_url"
}

val ILOA = "https://iloa.gg/character/"
val LOAWA = "https://loawa.com/char/"
val KLOA = "https://m.kloa.gg/characters/"

val UrlList = arrayListOf("일로아" to ILOA, "로아와" to LOAWA, "클로아" to KLOA)
val UrlNameList = UrlList.map { it.first }
val UrlAddressList = UrlList.map { it.second }