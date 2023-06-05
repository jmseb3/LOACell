package com.wonddak.sharedfb

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform