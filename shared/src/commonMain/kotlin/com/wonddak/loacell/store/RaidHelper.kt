package com.wonddak.loacell.store

import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
import kotlin.jvm.JvmField

data class FBRaidInfo(
    val title: String = "",
    val type: String = RaidType.ETC.name,
    val difficulty: String = Difficulty.Normal.name,
    val startGateNumber: Int = 0,
    val endGateNumber: Int = 0,
    @field:JvmField
    val isFinish: Boolean = false,
    val party1: List<String> = List(4) { "" },
    val party2: List<String> = List(4) { "" }

) {
    fun toMap(): HashMap<Any?, Any> {
        val data = HashMap<Any?, Any>()
        data["title"] = title
        data["type"] = type
        data["difficulty"] = difficulty
        data["startGateNumber"] = startGateNumber
        data["endGateNumber"] = endGateNumber
        data["finish"] = isFinish
        data["party1"] = party1
        data["party2"] = party2
        return data
    }
}

expect object RaidHelper {
    //레이드 정보를 추가한다.
    fun add(
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Int,
        endGateNumber: Int,
        failAction: (e: String) -> Unit ,
        successAction: () -> Unit
    )
    //레이드 정보를 삭제한다.
    fun delete(
        roomId: String,
        raidId: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    )

    // 파티 리스트를 업데이트 한다.
    fun updatePartList(
        roomId: String,
        raidId: String,
        partyIndex: Int,
        partyList: List<String>,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    )
}