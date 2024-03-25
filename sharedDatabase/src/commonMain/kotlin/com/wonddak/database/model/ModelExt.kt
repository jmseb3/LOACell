package com.wonddak.database.model

fun String.convertType(): RaidType {
    RaidType.entries.forEach {
        if (it.name == this) {
            return  it
        }
    }
    return RaidType.ETC
}

fun String.convertDifficulty(): Difficulty {
    Difficulty.entries.forEach {
        if (it.name == this) {
            return  it
        }
    }
    return Difficulty.Normal
}

fun Long.convertToDay() :Day {
    Day.entries.forEach {
        if (it.index == this.toInt()) {
            return  it
        }
    }
    return  Day.NONE
}