package com.wonddak.loacell.ext

import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.util.TimeHelper
import dev.icerock.moko.resources.ImageResource

fun RaidInfo.getAllPartyList(): List<String> = this.party1characterList + this.party2characterList

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

fun RaidInfo.getPartNameList() :List<String> {
    val characterNameInParty = mutableListOf<String>()
    characterNameInParty.addAll(this.party1characterList.filter { it.isNotEmpty() })
    characterNameInParty.addAll(this.party2characterList.filter { it.isNotEmpty() })
    return  characterNameInParty
}