package com.wonddak.loacell

import com.wonddak.loacell.model.RaidType
import dev.icerock.moko.resources.ImageResource

fun Character.getLevel(): Float {
    return this.level.replace(",","").toFloat()
}
