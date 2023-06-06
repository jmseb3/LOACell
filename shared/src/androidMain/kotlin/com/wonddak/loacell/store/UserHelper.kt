package com.wonddak.loacell.store

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.sharedapi.model.CharacterInfo

actual object UserHelper {

    private fun getUserDocRef(
        roomId: String,
        name: String
    ): DocumentReference =
        Firebase.firestore.collection("rooms").document(roomId).collection("users").document(name)

    actual fun add(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        val fbUserInfo = FBUSerInfo(
            representativeCharacter,
            characterList.map { it.characterName }
        )
        val userRoom = getUserDocRef(roomId, name)

        userRoom.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    failAction("이미 존재하는 이름입니다.")
                } else {
                    userRoom.set(fbUserInfo)
                        .addOnSuccessListener {
                            successAction()
                        }
                        .addOnFailureListener {
                            failAction(it.localizedMessage ?: "unknown error")
                        }
                }
            }
            .addOnFailureListener {
                failAction(it.localizedMessage ?: "unknown error")
            }

    }

    actual fun updateRepresentativeCharacter(
        roomId: String,
        name: String,
        representativeCharacter: String
    ) {
        getUserDocRef(roomId,name).update("representativeCharacter",representativeCharacter)
    }

    actual fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getUserDocRef(roomId,name)
            .delete()
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener {
                failAction(it.localizedMessage ?: "unknown error")
            }
    }
}