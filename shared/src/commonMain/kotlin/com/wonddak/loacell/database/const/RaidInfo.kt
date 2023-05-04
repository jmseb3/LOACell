package com.wonddak.loacell.database.const


enum class RaidType {
    VALTAN,
    VYKAS,
    KOUKU,
    ABRELSHUD,
    ILLIALAN,
    KAYANGEL,
    IVORYTOWER,
    ETC;

    fun toKorString(): String {
        return when(this) {
            VALTAN -> "발탄"
            VYKAS -> "비아키스"
            KOUKU -> "쿠크세이튼"
            ABRELSHUD -> "아브렐슈드"
            ILLIALAN -> "일리아칸"
            KAYANGEL -> "카앙겔"
            IVORYTOWER -> "상아탑"
            ETC -> "기타"
        }
    }
}