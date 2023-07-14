package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.database.model.Day
import com.wonddak.database.model.RaidType
import com.wonddak.database.model.convertDifficulty
import com.wonddak.database.model.convertToDay
import com.wonddak.database.model.convertType
import com.wonddak.loacell.model.Difficulty
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
        "day" to day.index,
        "hour" to hour,
        "minute" to minute
    )

    fun getMinLevelText() :String {
        val minLevel =  this.type.getMinLevel(difficulty,endGateNumber)
        return if (minLevel == 0) "제한 없음" else minLevel.toString()
    }
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
    fun add(
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Int,
        endGateNumber: Int,
        failAction: (e: Error) -> Unit,
        successAction: () -> Unit
    ) {
        val fbRaidInfo = FBRaidInfo(
            title = title,
            type = type,
            difficulty = difficulty,
            startGateNumber = startGateNumber,
            endGateNumber = endGateNumber
        )
        RefHelper.getRaidsRef(roomId).document()
            .set(
                fbRaidInfo.toMap(),
                successAction = successAction,
                failAction = failAction
            )
    }

    private fun addEmptyDay(roomId: String, raidId: String) {
        val emptyDayMap = mapOf(
            "day" to -1,
            "hour" to 0,
            "minute" to 0
        )
        RefHelper.getRaidRef(roomId, raidId).update(emptyDayMap)
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

    fun updateTitle(
        roomId: String,
        raidId: String,
        title: String
    ) {
        updateField(roomId, raidId, "title", title)
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

    fun updateDay(
        roomId: String,
        raidId: String,
        day: Day,
    ) {
        updateField(roomId, raidId, "day", day.index)
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
                    val raidId = it.id
                    println("JWH Listen raidId : $raidId")
                    val title = it.data!!["title"] as String
                    val typeString = it.data!!["type"] as String
                    val difficultyString = it.data!!["difficulty"] as String
                    val startGateNumber = it.data!!["startGateNumber"] as Long
                    val endGateNumber = it.data!!["endGateNumber"] as Long
                    val isFinish = it.data!!["finish"] as Boolean
                    val party1 = it.data!!["party1"] as List<String>
                    val party2 = it.data!!["party2"] as List<String>

                    val day = it.data["day"] as Long?
                    val hour = it.data["hour"] as Long?
                    val minute = it.data["minute"] as Long?

                    //이미 값이 있는 경우
                    if (raidId in dbRaidList) {
                        //업데이트
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
                            party2
                        )
                        dbRaidList.remove(raidId)
                        if (day == null && hour == null && minute == null) {
                            println("JWH $raidId none Day Date.. update")
                            addEmptyDay(roomId, raidId)
                        } else if (day != null && hour != null && minute != null) {
                            db.raidInfoQueriesHelper.updateRaidInfoDay(
                                roomId,
                                raidId,
                                day,
                                hour,
                                minute
                            )
                        }
                    } else {
                        //없는 경우 추가
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
                println("JWH Fail with error : ${it?.errorMsg}")
            }
        )
    }
}