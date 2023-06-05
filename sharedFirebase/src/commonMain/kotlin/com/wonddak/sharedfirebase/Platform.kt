package com.wonddak.sharedfirebase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

