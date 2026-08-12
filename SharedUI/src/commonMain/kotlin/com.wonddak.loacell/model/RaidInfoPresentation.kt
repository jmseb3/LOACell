package com.wonddak.loacell.model

import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.util.TimeHelper

val RaidInfo.raidItem: RaidData?
    get() = RaidItem.findByName(type)

val RaidInfo.level: Level?
    get() = raidItem?.level?.find { it.difficulty == difficulty }

fun RaidInfo.getRaidText(): String =
    "${Translate.getTranslate(type)} - ${Translate.getTranslate(difficulty)}"

fun RaidInfo.getMaxParty(): Int = level?.partySize ?: 0

fun RaidInfo.getMinLevel(): Int = level?.let {
    runCatching { it.info[endGateNumber - 1] }.getOrDefault(it.info.last())
} ?: 0

fun RaidInfo.makeGateText(): String = level?.let {
    if (it.differentPerGate) "$startGateNumber ~ $endGateNumber 관문" else "1 ~ ${it.maxGate} 관문"
} ?: "관문 정보 없음"

fun RaidInfo.getImage() = RaidItem.getImage(type.lowercase())

fun RaidInfo.getTimeText(): String = TimeHelper.makeTimeText(hour, minute)

fun RaidInfo.getDayText(): String = "${day.text} ${getTimeText()}"

fun RaidInfo.getAllPartyList(): List<String> = when (getMaxParty()) {
    1 -> party1characterList
    2 -> party1characterList + party2characterList
    4 -> party1characterList + party2characterList + party3characterList + party4characterList
    else -> emptyList()
}

fun RaidInfo.getPartNameList(): Set<String> = getAllPartyList().filter(String::isNotEmpty).toSet()
