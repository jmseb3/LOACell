package com.wonddak.database.model

enum class Day(val index: Int, val text: String) {
    NONE(-1, ""),
    MON(0, "월"),
    TUE(1, "화"),
    WED(2, "수"),
    THU(3, "목"),
    FRI(4, "금"),
    SAT(5, "토"),
    SUN(6, "일")
}