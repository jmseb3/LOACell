package com.wonddak.loacell.database.model

enum class Difficulty {
    Normal,
    Hard,
    Hell,
    ExtremeNormal,
    ExtremeHard;

    fun toKorString(): String {
        return when (this) {
            Normal -> "노말"
            Hard -> "하드"
            Hell -> "헬"
            ExtremeNormal -> "익스트림(노말)"
            ExtremeHard -> "익스트림(노말)"
        }
    }
}