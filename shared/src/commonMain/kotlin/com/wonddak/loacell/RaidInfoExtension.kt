package com.wonddak.loacell

import com.wonddak.loacell.model.RaidType
import dev.icerock.moko.resources.ImageResource

fun RaidInfo.getMinLevel(): Int {
    return this.type!!.getMinLevel(this.Difficulty!!, this.endGateNumber.toInt())
}

fun RaidInfo.getRaidText(): String {
    return "${this.type!!.toKorString()} - ${this.Difficulty!!.toKorString()}"
}

fun RaidInfo.getMaxParty(): Int {
    return this.type!!.getMaxParty()
}

fun RaidInfo.makeGateText(): String {
    return when (this.type!!) {
        RaidType.ABRELSHUD -> {
            "${this.startGateNumber * 2 - 1} ~ ${this.endGateNumber * 2} 관문"
        }

        RaidType.ETC -> {
            "관문 정보 없음"
        }

        else -> {
            "1 ~ ${this.type!!.getMaxGate()} 관문"
        }
    }
}

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

fun RaidInfo.getPartyList(): List<String> {
    val result = mutableListOf<String>()
    result.addAll(this.party1characterList.filter { it.isNotEmpty() })
    result.addAll(this.party2characterList.filter { it.isNotEmpty() })
    return  result
}