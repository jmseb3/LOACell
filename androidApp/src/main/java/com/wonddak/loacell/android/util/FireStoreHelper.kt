package com.wonddak.loacell.android.util

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
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

    fun addUser(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: (e: Exception) -> Unit = {},
        successAction: () -> Unit = {}
    ) {
        Firebase.firestore.let { fs ->
            val userData = HashMap<String, Any>()
            userData["representativeCharacter"] = representativeCharacter
            userData["characterList"] = characterList.map { it.characterName }
            userData["show"] = true
            fs.collection("rooms")
                .document(roomId)
                .collection("users")
                .document(name)
                .let { userRoom ->
                    userRoom.get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                userRoom.update(userData)
                                    .addOnSuccessListener {
                                        characterList.forEach {
                                            addCharacter(it)
                                        }
                                        successAction()
                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                        failAction(e)
                                    }
                            } else {
                                userRoom.set(userData)
                                    .addOnSuccessListener {
                                        characterList.forEach {
                                            addCharacter(it)
                                        }
                                        successAction()
                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                        failAction(e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            failAction(e)
                        }
                }
        }
    }

    fun updateUserCharacter(
        roomId: String,
        name: String,
        representativeCharacter: String
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("users")
            .document(name)
            .update("representativeCharacter", representativeCharacter)
    }

    fun deleteUser(
        roomId: String,
        userName: String,
        successAction: () -> Unit
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("users")
            .document(userName).let { userNameDoc ->
                userNameDoc.get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            userNameDoc.update("show", false)
                                .addOnSuccessListener {
                                    successAction()
                                }
                        }
                    }
                    .addOnFailureListener {

                    }
            }

    }

    fun addCharacter(
        character: CharacterInfo
    ) {
        Firebase.firestore.let { fs ->
            val room = fs.collection("characters")
            val nowTime = System.currentTimeMillis()

            val data = HashMap<String, Any>()
            data["level"] = character.itemMaxLevel
            data["server"] = character.serverName
            data["className"] = character.characterClassName
            data["timeStamp"] = nowTime

            room.document(character.characterName).let { characterRoom ->
                characterRoom.get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val lastTime = it.data!!["timeStamp"] as Long
                            if (lastTime + 3_600L * 1_000L > nowTime) {
                                characterRoom.update(data)
                                    .addOnFailureListener {
                                        it.printStackTrace()
                                    }
                            } else {

                            }
                        } else {
                            characterRoom.set(data)
                                .addOnFailureListener {
                                    it.printStackTrace()
                                }
                        }
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    fun observeCharacters(
        characterName: String,
        successAction: (className: String, level: String, server: String) -> Unit
    ) {
        Firebase.firestore.collection("characters")
            .document(characterName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("JWH", "Listen failed.", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    Log.d("JWH", "Listen characters == $characterName ${value.data.toString()}")
                    value.data?.let { data ->
                        val className = data["className"] as String
                        val level = data["level"] as String
                        val server = data["server"] as String
                        successAction(
                            className, level, server
                        )
                    }
                } else {
                    Log.d("JWH", "Listen characters value null")
                }
            }
    }
}