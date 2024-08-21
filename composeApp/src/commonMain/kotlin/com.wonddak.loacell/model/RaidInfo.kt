package com.wonddak.loacell.model

import com.wonddak.loacell.store.CommonDocumentSnapshot
import com.wonddak.loacell.util.TimeHelper
import loacell.composeapp.generated.resources.Res
import loacell.composeapp.generated.resources.raid_abrelshud
import loacell.composeapp.generated.resources.raid_behemoth
import loacell.composeapp.generated.resources.raid_echidna
import loacell.composeapp.generated.resources.raid_egir
import loacell.composeapp.generated.resources.raid_illiakan
import loacell.composeapp.generated.resources.raid_ivory_tower
import loacell.composeapp.generated.resources.raid_kamen
import loacell.composeapp.generated.resources.raid_kayangel
import loacell.composeapp.generated.resources.raid_kouku
import loacell.composeapp.generated.resources.raid_valtan
import loacell.composeapp.generated.resources.raid_vykas
import org.jetbrains.compose.resources.DrawableResource

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
    val type: String,
    val difficulty: String,
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
            this[RaidInfoField.TYPE] as String,
            this[RaidInfoField.DIFFICULTY] as String,
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

fun Long.convertToDay(): Day {
    Day.entries.forEach {
        if (it.index == this.toInt()) {
            return it
        }
    }
    return Day.NONE
}

fun RaidInfo.getMinLevel(): Int {
    return 0
//    return this.type.getMinLevel(this.difficulty, this.endGateNumber)
}

fun RaidInfo.getRaidText(): String {
    return  ""
//    return "${this.type.toKorString()} - ${this.difficulty.toKorString()}"
}

fun RaidInfo.getMaxParty(): Int {
    return  1
//    return this.type.getMaxParty()
}

fun RaidInfo.makeGateText(): String {
    return ""
//    return when (this.type) {
//        RaidType.ETC -> {
//            "관문 정보 없음"
//        }
//
//        else -> {
//            "1 ~ ${this.type.getMaxGate(this.difficulty)} 관문"
//        }
//    }
}

fun RaidInfo.getImg(): DrawableResource? {
    return when (this.type) {
//        RaidType.VALTAN -> {
//            Res.drawable.raid_valtan
//        }
//
//        RaidType.VYKAS -> {
//            Res.drawable.raid_vykas
//        }
//
//        RaidType.KOUKU -> {
//            Res.drawable.raid_kouku
//        }
//
//        RaidType.ABRELSHUD -> {
//            Res.drawable.raid_abrelshud
//        }
//
//        RaidType.ILLIAKAN -> {
//            Res.drawable.raid_illiakan
//        }
//
//        RaidType.KAMEN -> {
//            Res.drawable.raid_kamen
//        }
//
//        RaidType.KAYANGEL -> {
//            Res.drawable.raid_kayangel
//        }
//
//        RaidType.IVORYTOWER -> {
//            Res.drawable.raid_ivory_tower
//        }
//
//        RaidType.ECHIDNA -> {
//            Res.drawable.raid_echidna
//        }
//
//        RaidType.BETHEMOTH -> {
//            Res.drawable.raid_behemoth
//        }
//
//        RaidType.EGIR -> {
//            Res.drawable.raid_egir
//        }

        else -> {
            null
        }
    }
}

fun RaidInfo.getDayText(): String =
    "${this.day.text} ${TimeHelper.makeTimeText(this.hour, this.minute)}"

/**
 * 모든 파티 리스트를 가져온다.
 */
fun RaidInfo.getAllPartyList(): List<String> {
    val maxParty = this.getMaxParty()

    return when (maxParty) {
        1 -> {
            this.party1characterList
        }

        2 -> {
            arrayListOf(
                this.party1characterList,
                this.party2characterList,
            ).flatten()
        }

        4 -> {
            arrayListOf(
                this.party1characterList,
                this.party2characterList,
                this.party3characterList,
                this.party4characterList
            ).flatten()
        }

        else -> {
            emptyList()
        }
    }
}

/**
 * partyIndex 번호로 partyList를 가져온다.
 */
fun RaidInfo.getPartyByIndex(index: Int): List<String> {
    return when (index) {
        0 -> {
            this.party1characterList
        }

        1 -> {
            this.party2characterList
        }

        2 -> {
            this.party3characterList
        }

        3 -> {
            this.party4characterList
        }

        else -> {
            throw IllegalArgumentException(
                """
                    잘못된 index 0~3
                """.trimIndent()
            )
        }
    }
}

/**
 * Party에 들어간 이름목록 집합을 가져온다.
 */
fun RaidInfo.getPartNameList(): Set<String> {
    return getAllPartyList()
        .toMutableSet()
        .also {
            it.remove("")
        }.toSet()
}