package com.wonddak.database.ext

import com.wonddak.loacell.Character

fun Character.getLevel(): Float {
    return this.level.replace(",","").toFloat()
}
