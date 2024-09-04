package com.wonddak.loacell.model

import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.store.CommonDocumentSnapshot
import com.wonddak.loacell.util.TimeHelper
import kotlinx.serialization.Serializable

object RaidInfoField {
    internal const val TITLE = "title"
    internal const val TYPE = "type"
    internal const val DIFFICULTY = "difficulty"
    internal const val START_GATE_NUMBER = "startGateNumber"
    internal const val END_GATE_NUMBER = "endGateNumber"
    internal const val FINISH = "finish"
    internal const val PARTY_1 = "party1"
    internal const val PARTY_2 = "party2"
    internal const val PARTY_3 = "party3"
    internal const val PARTY_4 = "party4"
    internal const val DAY = "day"
    internal const val HOUR = "hour"
    internal const val MINUTE = "minute"
}

@Serializable
data class RaidInfo(
    var raidId: String,
    var roomId: String,
    var title: String,
    var type: String,
    var difficulty: String,
    var startGateNumber: Int,
    var endGateNumber: Int,
    var isFinish: Boolean,
    var party1characterList: List<String>,
    var party2characterList: List<String>,
    var party3characterList: List<String>,
    var party4characterList: List<String>,
    var dayIndex: Long,
    var hour: Int,
    var minute: Int,
) {
    constructor(roomId: String, type: String, difficulty: String) : this(
        "",
        roomId,
        "",
        type,
        difficulty,
        1,
        1,
        false,
        List(4) { "" },
        List(4) { "" },
        List(4) { "" },
        List(4) { "" },
        -1,
        0,
        0
    )

    val day: Day
        get() = dayIndex.convertToDay()

    fun toMap() = mapOf(
        RaidInfoField.TITLE to title,
        RaidInfoField.TYPE to type,
        RaidInfoField.DIFFICULTY to difficulty,
        RaidInfoField.START_GATE_NUMBER to startGateNumber,
        RaidInfoField.END_GATE_NUMBER to endGateNumber,
        RaidInfoField.FINISH to isFinish,
        RaidInfoField.PARTY_1 to party1characterList,
        RaidInfoField.PARTY_2 to party2characterList,
        RaidInfoField.PARTY_3 to party3characterList,
        RaidInfoField.PARTY_4 to party4characterList,
        RaidInfoField.DAY to dayIndex,
        RaidInfoField.HOUR to hour,
        RaidInfoField.MINUTE to minute
    )

    val raidItem: RaidData?
        get() = RaidItem.findByName(type)

    val level: Level?
        get() = raidItem?.level?.find { it.difficulty == difficulty }

    fun getRaidText(): String {
        return "${Translate.getTranslate(type)} - ${Translate.getTranslate(difficulty)}"
    }

    fun getMaxParty(): Int {
        return level?.partySize ?: 0
    }

    fun getMinLevel(): Int {
        return level?.let {
            runCatching {
                it.info[endGateNumber - 1]
            }.getOrDefault(it.info.last())
        } ?: 0
    }

    fun makeGateText(): String {
        return level?.let {
            if (it.differentPerGate) {
                "$startGateNumber ~ $endGateNumber 관문"
            } else {
                "1 ~ ${it.maxGate} 관문"
            }
        } ?: "관문 정보 없음"
    }

    fun getImage() = RaidItem.getImage(type.lowercase())

    fun getTimeText(): String =
        TimeHelper.makeTimeText(this.hour, this.minute)

    fun getDayText(): String =
        "${this.day.text} ${getTimeText()}"

    /**
     * 모든 파티 리스트를 가져온다.
     */
    fun getAllPartyList(): List<String> {
        val maxParty = this.getMaxParty()

        return when (maxParty) {
            1 -> {
                this.party1characterList
            }

            2 -> {
                arrayListOf(
                    this.party1characterList,
                    this.party2characterList,
                ).flatten()
            }

            4 -> {
                arrayListOf(
                    this.party1characterList,
                    this.party2characterList,
                    this.party3characterList,
                    this.party4characterList
                ).flatten()
            }

            else -> {
                emptyList()
            }
        }
    }

    /**
     * partyIndex 번호로 partyList를 가져온다.
     */
    fun getPartyByIndex(index: Int): List<String> {
        return when (index) {
            0 -> {
                this.party1characterList
            }

            1 -> {
                this.party2characterList
            }

            2 -> {
                this.party3characterList
            }

            3 -> {
                this.party4characterList
            }

            else -> {
                throw IllegalArgumentException(
                    """
                        잘못된 index 0~3
                    """.trimIndent()
                )
            }
        }
    }

    /**
     * Party에 들어간 이름목록 집합을 가져온다.
     */
    fun getPartNameList(): Set<String> {
        return getAllPartyList()
            .toMutableSet()
            .also {
                it.remove("")
            }.toSet()
    }
}

fun CommonDocumentSnapshot.toRaidInfo(roomId: String): RaidInfo {
    return with(this.data!!) {
        RaidInfo(
            this@toRaidInfo.id,
            roomId,
            this[RaidInfoField.TITLE] as String,
            this[RaidInfoField.TYPE] as String,
            this[RaidInfoField.DIFFICULTY] as String,
            (this[RaidInfoField.START_GATE_NUMBER] as Long).toInt(),
            (this[RaidInfoField.END_GATE_NUMBER] as Long).toInt(),
            this[RaidInfoField.FINISH] as Boolean,
            this[RaidInfoField.PARTY_1] as List<String>,
            this[RaidInfoField.PARTY_2] as List<String>,
            runCatching {
                this[RaidInfoField.PARTY_3] as List<String>
            }.getOrDefault(List(4) { "" }),
            runCatching {
                this[RaidInfoField.PARTY_4] as List<String>
            }.getOrDefault(List(4) { "" }),
            runCatching {
                (this[RaidInfoField.DAY] as Long)
            }.getOrDefault(-1),
            runCatching {
                (this[RaidInfoField.HOUR] as Long).toInt()
            }.getOrDefault(0),
            runCatching {
                (this[RaidInfoField.MINUTE] as Long).toInt()
            }.getOrDefault(0),
        )
    }
}

fun Long.convertToDay(): Day {
    Day.entries.forEach {
        if (it.index == this.toInt()) {
            return it
        }
    }
    return Day.NONE
}

