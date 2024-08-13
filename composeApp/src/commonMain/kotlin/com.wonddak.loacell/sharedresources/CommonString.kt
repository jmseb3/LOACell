package com.wonddak.loacell.sharedresources

import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.login_anonymous
import loacell.composeapp.generated.resources.login_info_1
import loacell.composeapp.generated.resources.login_info_2
import loacell.composeapp.generated.resources.login_progress
import org.jetbrains.compose.resources.StringResource


object CommonString {

    object Login {
        fun getInfo1(): StringResource = Res.string.login_info_1
        fun getInfo2(): StringResource = Res.string.login_info_2
        fun getAnonymous(): StringResource = Res.string.login_anonymous
        fun getProgress(): StringResource = Res.string.login_progress
    }
}