package com.wonddak.loacell.model

import com.wonddak.loacell.database.model.Day
import com.wonddak.loacell.database.model.Difficulty
import com.wonddak.loacell.database.model.RaidType
import com.wonddak.loacell.database.model.convertDifficulty
import com.wonddak.loacell.database.model.convertToDay
import com.wonddak.loacell.database.model.convertType
import com.wonddak.loacell.store.CommonDocumentSnapshot

object RaidInfoField {
    internal const val TITLE = "title"
    internal const val TYPE = "type"
    internal const val DIFFICULTY = "difficulty"
    internal const val START_GATE_NUMBER = "startGateNumber"
    internal const val END_GATE_NUMBER = "endGateNumber"
    internal const val FINISH = "finish"
    internal const val PARTY_1 = "party1"
    internal const val PARTY_2 = "party2"
    internal const val PARTY_3 = "party3"
    internal const val PARTY_4 = "party4"
    internal const val DAY = "day"
    internal const val HOUR = "hour"
    internal const val MINUTE = "minute"
}

data class RaidInfo(
    val raidId: String,
    val roomId: String,
    val title: String,
    val type: RaidType,
    val difficulty: Difficulty,
    val startGateNumber: Int,
    val endGateNumber: Int,
    val isFinish: Boolean,
    val party1characterList: List<String>,
    val party2characterList: List<String>,
    val party3characterList: List<String>,
    val party4characterList: List<String>,
    val day: Day,
    val hour: Int,
    val minute: Int,
)

fun CommonDocumentSnapshot.toRaidInfo(roomId: String): RaidInfo {
    return with(this.data!!) {
        RaidInfo(
            this@toRaidInfo.id,
            roomId,
            this[RaidInfoField.TITLE] as String,
            (this[RaidInfoField.TYPE] as String).convertType(),
            (this[RaidInfoField.DIFFICULTY] as String).convertDifficulty(),
            (this[RaidInfoField.START_GATE_NUMBER] as Long).toInt(),
            (this[RaidInfoField.END_GATE_NUMBER] as Long).toInt(),
            this[RaidInfoField.FINISH] as Boolean,
            this[RaidInfoField.PARTY_1] as List<String>,
            this[RaidInfoField.PARTY_2] as List<String>,
            runCatching {
                this[RaidInfoField.PARTY_3] as List<String>
            }.getOrDefault(List(4) { "" }),
            runCatching {
                this[RaidInfoField.PARTY_4] as List<String>
            }.getOrDefault(List(4) { "" }),
            runCatching {
                (this[RaidInfoField.DAY] as Long).convertToDay()
            }.getOrDefault(Day.NONE),
            runCatching {
                (this[RaidInfoField.HOUR] as Long).toInt()
            }.getOrDefault(0),
            runCatching {
                (this[RaidInfoField.MINUTE] as Long).toInt()
            }.getOrDefault(0),
        )
    }
}