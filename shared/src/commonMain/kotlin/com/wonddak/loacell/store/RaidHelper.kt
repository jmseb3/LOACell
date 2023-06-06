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
    fun toMap() = mapOf(
        "title" to title,
        "type" to type,
        "difficulty" to difficulty,
        "startGateNumber" to startGateNumber,
        "endGateNumber" to endGateNumber,
        "endGateNumber" to endGateNumber,
        "finish" to isFinish,
        "party1" to party1,
        "party2" to party2
    )
}

object RaidHelper {
    private fun getRaidsRef(roomId: String): CommonCollection =
        getFireStore().collection("rooms").document(roomId).collection("raidInfo")

    private fun getRaidRef(roomId: String, raidId: String): CommonDocument =
        getRaidsRef(roomId).document(raidId)
    //레이드 정보를 추가한다.
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
            type = type.name,
            difficulty = difficulty.name,
            startGateNumber = startGateNumber,
            endGateNumber = endGateNumber
        )
        getRaidsRef(roomId).document()
            .set(
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
        getRaidRef(roomId, raidId).delete(
            successAction = successAction,
            failAction = failAction
        )
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
        getRaidRef(roomId, raidId).update(
            field = "party$partyIndex",
            value = partyList,
            successAction = successAction,
            failAction = failAction
        )
    }
}