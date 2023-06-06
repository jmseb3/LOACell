package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRDocumentReference
import cocoapods.FirebaseFirestore.FIRFirestore
import com.wonddak.sharedapi.model.CharacterInfo
import platform.QuartzCore.CACurrentMediaTime

actual object UserHelper {

    private fun getUserDocRef(
        roomId: String,
        name: String
    ): FIRDocumentReference =
        FIRFirestore.firestore()
            .collectionWithPath("rooms")
            .documentWithPath(roomId)
            .collectionWithPath("users")
            .documentWithPath(name)


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

        userRoom.getDocumentWithCompletion { firDocumentSnapshot, nsError ->
            if (nsError != null) {
                failAction(nsError.localizedDescription)
            } else {
                if (firDocumentSnapshot?.exists == true) {
                    failAction("이미 존재하는 이름입니다.")
                } else {
                    userRoom.setData(fbUserInfo.toMap()) { err ->
                        if (err == null) {
                            successAction()
                        } else {
                            failAction(err.localizedDescription)
                        }
                    }
                }
            }
        }
    }

    actual fun updateRepresentativeCharacter(
        roomId: String,
        name: String,
        representativeCharacter: String
    ) {
        getUserDocRef(
            roomId,
            name
        ).updateData(mapOf("representativeCharacter" to representativeCharacter))
    }

    actual fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getUserDocRef(roomId, name)
            .deleteDocumentWithCompletion { err ->
                if (err == null) {
                    successAction()
                } else {
                    failAction(err.localizedDescription)
                }
            }
    }
}