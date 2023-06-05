package com.wonddak.sharedfb.store

import android.util.Log
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


actual object RoomHelper {
    actual fun syncInfo(
        userId: String,
        successPerDocAction: (id: String, roomInfo: FBRoomInfo) -> Unit,
        failAction: (error :String) -> Unit,
        successAction: () -> Unit
    ) {
        Firebase.firestore.collection("rooms").where(
            Filter.or(
                Filter.equalTo("owner", userId),
                Filter.arrayContains("anonymousUser", userId),
                Filter.arrayContains("editableUser", userId),
                Filter.arrayContains("enterUser", userId)
            )
        ).get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val id = document.id
                    val fbRoomInfo = document.toObject<FBRoomInfo>()
                    Log.d("JWH", "$id => $fbRoomInfo")
                    successPerDocAction(id,fbRoomInfo)
                }
                successAction()
            }
            .addOnFailureListener { exception ->
                Log.w("JWH", "Error getting documents: ", exception)
                failAction(exception.message ?: "error")
            }
    }
}