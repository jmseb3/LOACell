package com.wonddak.loacell.store

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wonddak.database.AppDataBase


actual object RoomHelper {
    private fun getRoomsRef(): CollectionReference = Firebase.firestore.collection("rooms")
    private fun getRoomRef(id: String): DocumentReference = getRoomsRef().document(id)
    actual fun syncInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error: String) -> Unit,
        successAction: () -> Unit
    ) {
        getRoomsRef()
            .where(
                Filter.or(
                    Filter.equalTo("owner", userId),
                    Filter.arrayContains("anonymousUser", userId),
                    Filter.arrayContains("editableUser", userId),
                    Filter.arrayContains("enterUser", userId)
                )
            )
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val id = document.id
                    val fbRoomInfo = document.toObject<FBRoomInfo>()
                    Log.d("JWH", "$id => $fbRoomInfo")
                    db.roomInfoQueriesHelper.addRoomInfo(
                        title = fbRoomInfo.title,
                        description = fbRoomInfo.description,
                        uniqueId = id,
                        owner = fbRoomInfo.owner
                    )
                }
                successAction()
            }
            .addOnFailureListener { exception ->
                Log.w("JWH", "Error getting documents: ", exception)
                failAction(exception.message ?: "error")
            }
    }

    actual fun checkExist(
        roomId: String,
        successAction: (password: String) -> Unit,
        failAction: () -> Unit
    ) {
        getRoomRef(roomId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    successAction(it.data!!["enterPassword"] as String)
                } else {
                    failAction()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                failAction()
            }
    }

    actual fun makeInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit
    ) {
        val data = FBRoomInfo(
            title = title,
            description = description,
            enterPassword = password,
            owner = owner
        )
        val ref = getRoomsRef()
            .document()
        ref.set(data)
            .addOnSuccessListener {
                successAction(ref.id)
            }
    }

    actual fun updateUser(
        roomId: String,
        userId: String,
        isAnonymous: Boolean,
        successAction: () -> Unit,
        failAction: (e: String?) -> Unit
    ) {
        val field = if (isAnonymous) "anonymousUser" else "enterUser"
        getRoomRef(roomId).update(
            field, FieldValue.arrayUnion(userId)
        )
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(it.localizedMessage)
            }

    }
}