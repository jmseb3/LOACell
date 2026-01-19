package com.wonddak.loacell.model

enum class Day(val index: Int, val text: String) {
    NONE(-1, ""),
    MON(0, "월"),
    TUE(1, "화"),
    WED(2, "수"),
    THU(3, "목"),
    FRI(4, "금"),
    SAT(5, "토"),
    SUN(6, "일");

    fun getList(): List<Day> {
        val startDay = this
        val result = listOf(MON, TUE, WED, THU, FRI, SAT, SUN)
        return result.subList(startDay.index, 7) + result.subList(0, startDay.index)
    }
}