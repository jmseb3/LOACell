package com.wonddak.loacell.android.util

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.api.model.CharacterInfo

object FireStoreHelper {

    fun addRoomInfo(
        title: String,
        description: String,
        successAction: (id: String) -> Unit
    ) {
        Firebase.firestore.let { fs ->
            val data = HashMap<String, Any>()
            data["title"] = title
            data["description"] = description
            data["timeStamp"] = System.currentTimeMillis()
            val newRooms = fs.collection("rooms").document()
            newRooms.set(data).addOnSuccessListener {
                successAction(newRooms.id)
            }
        }
    }

    fun addUserAndCharacterInfo(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: () -> Unit = {},
        successAction: () -> Unit
    ) {
        fun updateCharacters(userRoom:DocumentReference)  {
            val room = userRoom.collection("characters")
            characterList.forEach { character ->
                val data = HashMap<String, Any>()
                data["level"] = character.itemMaxLevel
                data["server"] = character.serverName
                data["className"] = character.characterClassName
                room.document(character.characterName).let { characterRoom ->
                    characterRoom.update(data)
                        .addOnSuccessListener {
                            successAction()
                        }
                        .addOnFailureListener {
                            characterRoom.set(data)
                                .addOnSuccessListener {
                                    successAction()
                                }
                                .addOnFailureListener {
                                    failAction()
                                }
                        }

                }
            }
        }
        Firebase.firestore.let { fs ->
            val userData = HashMap<String, Any>()
            userData["representativeCharacter"] = representativeCharacter
            userData["timeStamp"] = FieldValue.serverTimestamp()
            userData["show"] = true
            fs.collection("rooms")
                .document(roomId)
                .collection("users")
                .document(name)
                .let { userRoom ->
                    userRoom.update(userData)
                        .addOnSuccessListener {
                            updateCharacters(userRoom)
                        }
                        .addOnFailureListener {
                            userRoom.set(userData)
                                .addOnSuccessListener {
                                    updateCharacters(userRoom)
                                }
                                .addOnFailureListener {
                                    failAction()
                                }
                        }
                }
        }
    }

    fun deleteUser(
        roomId: String,
        userName: String,
        successAction:() ->Unit
    ){
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("users")
            .document(userName).update("show",false)
            .addOnSuccessListener {
                successAction()
            }
    }
}