package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRCollectionReference
import cocoapods.FirebaseFirestore.FIRDocumentReference
import cocoapods.FirebaseFirestore.FIRFirestore
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType

actual object RaidHelper {
    private fun getRaidsRef(roomId: String): FIRCollectionReference =
        FIRFirestore.firestore().collectionWithPath("rooms").documentWithPath(roomId)
            .collectionWithPath("raidInfo")

    private fun getRaidRef(roomId: String, raidId: String): FIRDocumentReference =
        getRaidsRef(roomId).documentWithPath(raidId)

    actual fun add(
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Int,
        endGateNumber: Int,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        val fbRaidInfo = FBRaidInfo(
            title = title,
            type = type.name,
            difficulty = difficulty.name,
            startGateNumber = startGateNumber,
            endGateNumber = endGateNumber
        )
        getRaidsRef(roomId).documentWithAutoID()
            .setData(fbRaidInfo.toMap()) { err ->
                if (err == null) {
                    successAction()
                } else {
                    failAction(err.localizedDescription)
                }
            }
    }

    actual fun delete(
        roomId: String,
        raidId: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getRaidRef(roomId, raidId).deleteDocumentWithCompletion { err ->
            if (err == null) {
                successAction()
            } else {
                failAction(err.localizedDescription)
            }
        }
    }

    actual fun updatePartList(
        roomId: String,
        raidId: String,
        partyIndex: Int,
        partyList: List<String>,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getRaidRef(roomId, raidId)
            .updateData(mapOf("party$partyList" to partyList)) { err ->
                if (err == null) {
                    successAction()
                } else {
                    failAction(err.localizedDescription)
                }
            }
    }
}