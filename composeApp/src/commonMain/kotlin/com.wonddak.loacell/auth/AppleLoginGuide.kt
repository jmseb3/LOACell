package com.wonddak.loacell.auth

interface AppleLoginGuide {

    fun linkToApple(fail: (String) -> Unit, success: () -> Unit)
    fun revokeToken(fail: (String) -> Unit, success: () -> Unit)
}