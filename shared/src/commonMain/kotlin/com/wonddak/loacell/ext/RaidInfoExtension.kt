package com.wonddak.loacell.ext

import com.wonddak.loacell.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.util.TimeHelper
import dev.icerock.moko.resources.ImageResource

fun RaidInfo.getMinLevel(): Int {
    return this.type.getMinLevel(this.Difficulty, this.endGateNumber.toInt())
}

fun RaidInfo.getRaidText(): String {
    return "${this.type.toKorString()} - ${this.Difficulty.toKorString()}"
}

fun RaidInfo.getMaxParty(): Int {
    return this.type.getMaxParty()
}

fun RaidInfo.makeGateText(): String {
    return when (this.type) {
        RaidType.ETC -> {
            "관문 정보 없음"
        }

        else -> {
            "1 ~ ${this.type.getMaxGate(this.Difficulty)} 관문"
        }
    }
}

fun RaidInfo.getImg(): ImageResource? {
    return when (this.type) {
        RaidType.VALTAN -> {
            SharedRes.images.raid_valtan
        }

        RaidType.VYKAS -> {
            SharedRes.images.raid_vykas
        }

        RaidType.KOUKU -> {
            SharedRes.images.raid_kouku
        }

        RaidType.ABRELSHUD -> {
            SharedRes.images.raid_abrelshud
        }

        RaidType.ILLIAKAN -> {
            SharedRes.images.raid_illiakan
        }

        RaidType.KAMEN -> {
            SharedRes.images.raid_kamen
        }

        RaidType.KAYANGEL -> {
            SharedRes.images.raid_kayangel
        }

        RaidType.IVORYTOWER -> {
            SharedRes.images.raid_ivory_tower
        }

        RaidType.ECHIDNA -> {
            SharedRes.images.raid_echidna
        }

        RaidType.BETHEMOTH -> {
            SharedRes.images.raid_behemoth
        }
        RaidType.EGIR -> {
            SharedRes.images.raid_egir
        }
        else -> {
            null
        }
    }
}

fun RaidInfo.getDayText(): String =
    "${this.day.text} ${TimeHelper.makeTimeText(this.hour.toInt(), this.minute.toInt())}"

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