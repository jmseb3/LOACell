package com.wonddak.loacell.ext

import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.database.model.RaidType
import dev.icerock.moko.resources.ImageResource

fun RaidInfo.getImg(): ImageResource? {
    return when (this.type!!) {
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
        RaidType.KAYANGEL -> {
            SharedRes.images.raid_kayangel
        }
        RaidType.IVORYTOWER -> {
            SharedRes.images.raid_ivory_tower
        }
        else -> {
            null
        }
    }
}