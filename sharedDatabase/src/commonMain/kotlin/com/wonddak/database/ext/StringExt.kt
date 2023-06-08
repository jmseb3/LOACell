package com.wonddak.database.ext

import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType

fun String.convertType(): RaidType {
    RaidType.values().forEach {
        if (it.name == this) {
            return  it
        }
    }
    return RaidType.ETC
}

fun String.convertDifficulty(): Difficulty {
    Difficulty.values().forEach {
        if (it.name == this) {
            return  it
        }
    }
    return Difficulty.Normal
}