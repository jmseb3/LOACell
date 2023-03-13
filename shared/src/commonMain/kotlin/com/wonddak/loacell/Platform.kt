package com.wonddak.loacell

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform