package com.wonddak.loacell.database.const

enum class Difficulty {
    Normal,
    Hard,
    Hell;

    fun toKorString(): String {
        return when (this) {
            Normal -> "노말"
            Hard -> "하드"
            Hell -> "헬"
        }
    }
}