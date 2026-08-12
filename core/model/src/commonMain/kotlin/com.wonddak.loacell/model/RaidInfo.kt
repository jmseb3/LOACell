package com.wonddak.loacell.model

import kotlinx.serialization.Serializable

@Serializable
data class RaidInfo(
    var raidId: String,
    var roomId: String,
    var title: String,
    var type: String,
    var difficulty: String,
    var startGateNumber: Int,
    var endGateNumber: Int,
    var isFinish: Boolean,
    var party1characterList: List<String>,
    var party2characterList: List<String>,
    var party3characterList: List<String>,
    var party4characterList: List<String>,
    var dayIndex: Long,
    var hour: Int,
    var minute: Int,
) {
    constructor(roomId: String, type: String, difficulty: String) : this(
        raidId = "",
        roomId = roomId,
        title = "",
        type = type,
        difficulty = difficulty,
        startGateNumber = 1,
        endGateNumber = 1,
        isFinish = false,
        party1characterList = List(4) { "" },
        party2characterList = List(4) { "" },
        party3characterList = List(4) { "" },
        party4characterList = List(4) { "" },
        dayIndex = -1,
        hour = 0,
        minute = 0,
    )

    val day: Day
        get() = Day.entries.firstOrNull { it.index == dayIndex.toInt() } ?: Day.NONE

    fun getPartyByIndex(index: Int): List<String> = when (index) {
        0 -> party1characterList
        1 -> party2characterList
        2 -> party3characterList
        3 -> party4characterList
        else -> throw IllegalArgumentException("잘못된 index 0~3")
    }
}
