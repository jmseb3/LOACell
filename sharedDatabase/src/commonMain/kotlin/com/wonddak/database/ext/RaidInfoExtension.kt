package com.wonddak.database.ext

import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo

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
        RaidType.ETC -> {
            "관문 정보 없음"
        }

        else -> {
            "1 ~ ${this.type!!.getMaxGate()} 관문"
        }
    }
}

fun RaidInfo.getAllPartyList(): List<String> = this.party1characterList + this.party2characterList