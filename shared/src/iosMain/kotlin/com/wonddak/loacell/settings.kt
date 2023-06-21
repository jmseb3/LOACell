@file:OptIn(ExperimentalSettingsApi::class)

package com.wonddak.loacell

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import platform.Foundation.NSUserDefaults


actual class Config() {
    private val delegate = NSUserDefaults.standardUserDefaults()
    actual val settings : FlowSettings = NSUserDefaultsSettings(delegate).toFlowSettings()

}