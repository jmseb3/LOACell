package com.wonddak.loacell.database.const

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