package com.wonddak.database.model

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

fun Long.convertToDay() :Day {
    Day.values().forEach {
        if (it.index == this.toInt()) {
            return  it
        }
    }
    return  Day.NONE
}