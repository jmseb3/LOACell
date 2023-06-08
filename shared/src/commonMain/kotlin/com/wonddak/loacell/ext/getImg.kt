package com.wonddak.loacell.ext

import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.SharedRes
import com.wonddak.loacell.model.RaidType
import dev.icerock.moko.resources.ImageResource

fun RaidInfo.getImg(): ImageResource? {
    return when (this.type!!) {
        RaidType.VALTAN -> {
            SharedRes.images.valtan
        }
        else -> {
            null
        }
    }
}