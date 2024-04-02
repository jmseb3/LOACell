package com.wonddak.loacell.ext

import com.wonddak.database.ext.getMaxParty
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.util.TimeHelper
import dev.icerock.moko.resources.ImageResource

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
    val findList = this.party1characterList.toMutableList()
    if (maxParty == 2) {
        findList.addAll(this.party2characterList)
    } else if (maxParty == 4) {
        findList.addAll(this.party3characterList)
        findList.addAll(this.party4characterList)
    }
    return findList
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
                올바르지 않은 index
            """.trimIndent()
            )
        }
    }
}

fun RaidInfo.getPartNameList(): Set<String> {
    return getAllPartyList()
        .toMutableSet()
        .also {
            it.remove("")
        }.toSet()
}