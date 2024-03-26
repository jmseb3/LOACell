package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.database.model.Day
import com.wonddak.database.model.Difficulty
import com.wonddak.database.model.RaidType
import com.wonddak.database.model.convertDifficulty
import com.wonddak.database.model.convertToDay
import com.wonddak.database.model.convertType
import kotlin.jvm.JvmField

data class FBRaidInfo(
    val title: String = "",
    val type: RaidType = RaidType.ETC,
    val difficulty: Difficulty = Difficulty.Normal,
    val startGateNumber: Int = 0,
    val endGateNumber: Int = 0,
    @field:JvmField
    val isFinish: Boolean = false,
    val party1: List<String> = List(4) { "" },
    val party2: List<String> = List(4) { "" },
    val party3: List<String> = List(4) { "" },
    val party4: List<String> = List(4) { "" },
    val day: Day = Day.NONE,
    val hour: Long = 0L,
    val minute: Long = 0L
) {
    fun toMap() = mapOf(
        "title" to title,
        "type" to type.name,
        "difficulty" to difficulty.name,
        "startGateNumber" to startGateNumber,
        "endGateNumber" to endGateNumber,
        "endGateNumber" to endGateNumber,
        "finish" to isFinish,
        "party1" to party1,
        "party2" to party2,
        "party3" to party3,
        "party4" to party4,
        "day" to day.index,
        "hour" to hour,
        "minute" to minute
    )

    fun getMinLevelText() :String {
        val minLevel =  this.type.getMinLevel(difficulty,endGateNumber)
        return if (minLevel == 0) "제한 없음" else minLevel.toString()
    }

    fun updateTitle(title: String) = this.copy(title = title)
    fun updateType(type: RaidType): FBRaidInfo {
        if (!type.accessibleDifficulty().contains(this.difficulty)) {
            return if (type != RaidType.ABRELSHUD) {
                this.copy(
                    type = type,
                    difficulty = Difficulty.Normal,
                    startGateNumber = 1,
                    endGateNumber = type.getMaxGate()
                )
            } else {
                this.copy(type = type, difficulty = Difficulty.Normal)
            }
        } else {
            return if (type != RaidType.ABRELSHUD) {
                this.copy(
                    type = type,
                    startGateNumber = 1,
                    endGateNumber = type.getMaxGate()
                )
            } else {
                this.copy(type = type)
            }
        }
    }
    fun updateDifficulty(difficulty: Difficulty) = this.copy(difficulty = difficulty)

    fun updateGate(start:Int,end:Int)  = this.copy(startGateNumber = start, endGateNumber = end)
    fun updateDay(day: Day)  = this.copy(day = day)

    fun resetDay() = this.copy(day = Day.NONE, hour = 0, minute = 0)

    fun updateTime(hour:Long,minute: Long) = this.copy(hour= hour, minute = minute)
    fun updateTimeHour(hour:Long) = this.copy(hour= hour)
    fun updateTimeMinute(minute: Long) = this.copy(minute = minute)
    fun difficultySelected(difficulty: Difficulty) :Boolean = this.difficulty == difficulty
    fun difficultyEnabled(difficulty: Difficulty) :Boolean = this.type.accessibleDifficulty().contains(difficulty)
}

object CommonRaidHelper {
    //레이드 정보를 추가한다.
    fun add(
        roomId: String,
        fbRaidInfo: FBRaidInfo,
        failAction: (e: Error) -> Unit,
        successAction: () -> Unit
    ) {
        RefHelper.getRaidsRef(roomId).document()
            .set(
                fbRaidInfo.toMap(),
                successAction = successAction,
                failAction = failAction
            )
    }

    fun update(
        roomId: String,
        raidId: String,
        fbRaidInfo: FBRaidInfo,
        failAction: (e: Error) -> Unit,
        successAction: () -> Unit
    ) {
        println("JWH3 - roomId:$roomId\nraidId:$raidId\nfb:$fbRaidInfo")
        RefHelper.getRaidRef(roomId, raidId)
            .update(
                fbRaidInfo.toMap(),
                successAction = successAction,
                failAction = failAction
            )
    }

    //레이드 정보를 삭제한다.
    fun delete(
        roomId: String,
        raidId: String,
        failAction: (e: Error) -> Unit,
        successAction: () -> Unit
    ) {
        RefHelper.getRaidRef(roomId, raidId).delete(
            successAction = successAction,
            failAction = failAction
        )
    }

    private fun updateField(
        roomId: String,
        raidId: String,
        field: String,
        value: Any
    ) {
        RefHelper.getRaidRef(roomId, raidId).update(
            field, value
        )
    }

    fun updateFinish(
        roomId: String,
        raidId: String,
        isFinish: Boolean
    ) {
        updateField(roomId, raidId, "finish", isFinish)
    }

    // 파티 리스트를 업데이트 한다.
    fun updatePartList(
        roomId: String,
        raidId: String,
        partyIndex: Int,
        partyList: List<String>,
        failAction: (e: Error) -> Unit,
        successAction: () -> Unit
    ) {
        RefHelper.getRaidRef(roomId, raidId).update(
            field = "party$partyIndex",
            value = partyList,
            successAction = successAction,
            failAction = failAction
        )
    }

    fun observe(
        roomId: String,
        db: AppDataBase
    ): CommonListenerRegistration {
        return RefHelper.getRaidsRef(roomId).getListenerRegistration(
            successAction = { value ->
                val dbRaidList =
                    db.raidInfoQueriesHelper.getAllByRoomIdValue(roomId).map { it.raidId }
                        .toMutableSet()

                value.documents.forEach {
                    println("JWH data - ${it.data}")
                    val raidId = it.id
                    val title = it.data!!["title"] as String
                    val typeString = it.data!!["type"] as String
                    val difficultyString = it.data!!["difficulty"] as String
                    val startGateNumber = it.data!!["startGateNumber"] as Long
                    val endGateNumber = it.data!!["endGateNumber"] as Long
                    val isFinish = it.data!!["finish"] as Boolean
                    val party1 = it.data!!["party1"] as List<String>
                    val party2 = it.data!!["party2"] as List<String>

                    //베히모스 관련 로직 추가
                    val party3 = runCatching { it.data?.get("party3") as List<*>}.getOrDefault(
                        List(4) {""}
                    ) as List<String>
                    val party4 = runCatching { it.data?.get("party4") as List<*>}.getOrDefault(
                        List(4) {""}
                    ) as List<String>

                    //일정 관련 로직
                    val day = runCatching { it.data?.get("day") as Long?}.getOrNull()
                    val hour = runCatching { it.data?.get("hour") as Long?}.getOrNull()
                    val minute = runCatching { it.data?.get("minute") as Long?}.getOrNull()

                    //이미 값이 있는 경우
                    if (raidId in dbRaidList) {
                        //업데이트
                        println("JWHa - update")

                        db.raidInfoQueriesHelper.updateRaidInfo(
                            raidId,
                            roomId,
                            title,
                            typeString.convertType(),
                            difficultyString.convertDifficulty(),
                            startGateNumber,
                            endGateNumber,
                            isFinish,
                            party1,
                            party2,
                            party3,
                            party4,
                            day,
                            hour,
                            minute
                        )
                        dbRaidList.remove(raidId)
                    } else {
                        //없는 경우 추가
                        println("JWHa - add")

                        db.raidInfoQueriesHelper.addRaidInfo(
                            raidId,
                            roomId,
                            title,
                            typeString.convertType(),
                            difficultyString.convertDifficulty(),
                            startGateNumber,
                            endGateNumber,
                            party1,
                            party2,
                            party3,
                            party4,
                            (day ?: -1L).convertToDay(),
                            hour ?: 0,
                            minute ?: 0
                        )
                    }
                }

                dbRaidList.forEach { name ->
                    db.raidInfoQueriesHelper.delete(name, roomId)
                }
            },
            failAction = {

            }
        )
    }
}