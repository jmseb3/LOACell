package com.wonddak.loacell.store

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType

actual object RaidHelper {
    private fun getRaidsRef(roomId: String): CollectionReference =
        Firebase.firestore.collection("rooms").document(roomId).collection("raidInfo")

    private fun getRaidRef(roomId: String, raidId: String): DocumentReference =
        getRaidsRef(roomId).document(raidId)

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
        getRaidsRef(roomId).document()
            .set(fbRaidInfo)
            .addOnSuccessListener { 
                successAction()
            }
            .addOnFailureListener { 
                failAction(it.localizedMessage ?: "unknown error")
            }
    }

    actual fun delete(
        roomId: String,
        raidId: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getRaidRef(roomId,raidId).delete()
            .addOnSuccessListener { 
                successAction()
            }
            .addOnFailureListener { 
                failAction(it.localizedMessage ?: "unknown error")
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
        getRaidRef(roomId,raidId)
            .update("party$partyIndex",partyList)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(it.localizedMessage ?:"unknown error")
            }
    }
}