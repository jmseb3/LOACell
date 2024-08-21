package com.wonddak.loacell.store

import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.toRaidInfo

//data class FBRaidInfo(
//    val title: String = "",
//    val type: RaidType = RaidType.ETC,
//    val difficulty: Difficulty = Difficulty.Normal,
//    val startGateNumber: Int = 0,
//    val endGateNumber: Int = 0,
//    @field:JvmField
//    val isFinish: Boolean = false,
//    val party1: List<String> = List(4) { "" },
//    val party2: List<String> = List(4) { "" },
//    val party3: List<String> = List(4) { "" },
//    val party4: List<String> = List(4) { "" },
//    val day: Day = Day.NONE,
//    val hour: Long = 0L,
//    val minute: Long = 0L
//) {
//    constructor(raidInfo: RaidInfo) : this(
//        raidInfo.title,
//        raidInfo.type,
//        raidInfo.Difficulty,
//        raidInfo.startGateNumber.toInt(),
//        raidInfo.endGateNumber.toInt(),
//        raidInfo.isFinish,
//        raidInfo.party1characterList,
//        raidInfo.party2characterList,
//        raidInfo.party3characterList,
//        raidInfo.party4characterList,
//        raidInfo.day,
//        raidInfo.hour,
//        raidInfo.minute,
//    )
//
//    fun toMap() = mapOf(
//        "title" to title,
//        "type" to type.name,
//        "difficulty" to difficulty.name,
//        "startGateNumber" to startGateNumber,
//        "endGateNumber" to endGateNumber,
//        "endGateNumber" to endGateNumber,
//        "finish" to isFinish,
//        "party1" to party1,
//        "party2" to party2,
//        "party3" to party3,
//        "party4" to party4,
//        "day" to day.index,
//        "hour" to hour,
//        "minute" to minute
//    )
//
//    fun getMinLevelText(): String {
//        val minLevel = this.type.getMinLevel(difficulty, endGateNumber)
//        return if (minLevel == 0) "제한 없음" else minLevel.toString()
//    }
//
//    fun updateTitle(title: String) = this.copy(title = title)
//    fun updateType(type: RaidType): FBRaidInfo {
//        if (!type.accessibleDifficulty().contains(this.difficulty)) {
//            return if (type != RaidType.ABRELSHUD) {
//                this.copy(
//                    type = type,
//                    difficulty = Difficulty.Normal,
//                    startGateNumber = 1,
//                    endGateNumber = type.getMaxGate(this.difficulty)
//                )
//            } else {
//                this.copy(type = type, difficulty = Difficulty.Normal)
//            }
//        } else {
//            return if (type != RaidType.ABRELSHUD) {
//                this.copy(
//                    type = type,
//                    startGateNumber = 1,
//                    endGateNumber = type.getMaxGate(this.difficulty)
//                )
//            } else {
//                this.copy(type = type)
//            }
//        }
//    }
//
//    fun updateDifficulty(difficulty: Difficulty) = this.copy(difficulty = difficulty)
//
//    fun updateGate(start: Int, end: Int) = this.copy(startGateNumber = start, endGateNumber = end)
//    fun updateDay(day: Day) = this.copy(day = day)
//
//    fun resetDay() = this.copy(day = Day.NONE, hour = 0, minute = 0)
//
//    fun updateTime(hour: Long, minute: Long) = this.copy(hour = hour, minute = minute)
//    fun updateTimeHour(hour: Long) = this.copy(hour = hour)
//    fun updateTimeMinute(minute: Long) = this.copy(minute = minute)
//    fun difficultySelected(difficulty: Difficulty): Boolean = this.difficulty == difficulty
//    fun difficultyEnabled(difficulty: Difficulty): Boolean =
//        this.type.accessibleDifficulty().contains(difficulty)
//
//    //레이드 정보가 수정될때 레벨에 맞지 않는 친구들을 다 지운다.
//    fun checkLevelParty(totalRoomInfo: TotalRoomInfo): FBRaidInfo {
//        val characterList = totalRoomInfo.characterLevelMap
//        val minLevel = this.type.getMinLevel(this.difficulty)
//
//        val checkLevel = { party: List<String> ->
//            val temp = Array(4) { "" }
//            party.forEachIndexed { tmpIndex, name ->
//                if (name.isNotEmpty()) {
//                    runCatching {
//                        characterList[name]!!
//                    }.onSuccess { level ->
//                        if (level < minLevel) {
//                            temp[tmpIndex] = ""
//                        } else {
//                            temp[tmpIndex] = name
//                        }
//                    }
//                }
//            }
//            temp.toList()
//        }
//        val empty = List(4) { "" }
//        when (this.type.getMaxParty()) {
//            4 -> {
//                return this.copy(
//                    party1 = checkLevel(party1),
//                    party2 = checkLevel(party2),
//                    party3 = checkLevel(party3),
//                    party4 = checkLevel(party4)
//                )
//            }
//
//            2 -> {
//                return this.copy(
//                    party1 = checkLevel(party1),
//                    party2 = checkLevel(party2),
//                    party3 = empty,
//                    party4 = empty
//                )
//            }
//
//            1 -> {
//                return this.copy(
//                    party1 = checkLevel(party1),
//                    party2 = empty,
//                    party3 = empty,
//                    party4 = empty
//                )
//            }
//        }
//
//        return this.copy(
//            party1 = checkLevel(party1),
//            party2 = checkLevel(party2),
//            party3 = checkLevel(party3),
//            party4 = checkLevel(party4)
//        )
//    }
//}

object CommonRaidHelper {
//    //레이드 정보를 추가한다.
//    fun add(
//        roomId: String,
//        fbRaidInfo: FBRaidInfo,
//        failAction: (e: Error) -> Unit,
//        successAction: () -> Unit
//    ) {
//        RefHelper.getRaidsRef(roomId).document()
//            .set(
//                fbRaidInfo.toMap(),
//                successAction = successAction,
//                failAction = failAction
//            )
//    }
//
//    fun update(
//        roomId: String,
//        raidId: String,
//        fbRaidInfo: FBRaidInfo,
//        failAction: (e: Error) -> Unit,
//        successAction: () -> Unit
//    ) {
//        RefHelper.getRaidRef(roomId, raidId)
//            .update(
//                fbRaidInfo.toMap(),
//                successAction = successAction,
//                failAction = failAction
//            )
//    }
//
//    //레이드 정보를 삭제한다.
//    fun delete(
//        roomId: String,
//        raidId: String,
//        failAction: (e: Error) -> Unit,
//        successAction: () -> Unit
//    ) {
//        RefHelper.getRaidRef(roomId, raidId).delete(
//            successAction = successAction,
//            failAction = failAction
//        )
//    }
//
//    private fun updateField(
//        roomId: String,
//        raidId: String,
//        field: String,
//        value: Any
//    ) {
//        RefHelper.getRaidRef(roomId, raidId).update(
//            field, value
//        )
//    }
//
//    fun updateFinish(
//        roomId: String,
//        raidId: String,
//        isFinish: Boolean
//    ) {
//        updateField(roomId, raidId, "finish", isFinish)
//    }
//
//    // 파티 리스트를 업데이트 한다.
//    fun updatePartList(
//        roomId: String,
//        raidId: String,
//        partyIndex: Int,
//        partyList: List<String>,
//        failAction: (e: Error) -> Unit,
//        successAction: () -> Unit
//    ) {
//        println("$$$ update Party$partyIndex to $partyList")
//        RefHelper.getRaidRef(roomId, raidId).update(
//            field = "party$partyIndex",
//            value = partyList,
//            successAction = successAction,
//            failAction = failAction
//        )
//    }

    fun observe(
        roomId: String,
        successAction: (List<RaidInfo>) -> Unit,
    ): CommonListenerRegistration {
        return RefHelper.getRaidsRef(roomId).getListenerRegistration(
            successAction = { value ->
                successAction(value.documents.map { it.toRaidInfo(roomId) })
            },
            failAction = {

            }
        )
    }
}