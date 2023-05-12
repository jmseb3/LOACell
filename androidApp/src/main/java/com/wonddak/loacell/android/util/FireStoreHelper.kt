package com.wonddak.loacell.android.util

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.api.model.CharacterInfo
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                                        successAction()
                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                        failAction(e)
                                    }
                            } else {
                                userRoom.set(userData)
                                    .addOnSuccessListener {
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
            .document(userName)
            .delete()
            .addOnSuccessListener {
                successAction()
            }
    }

    fun addRaidInfo(
        roomId: String,
        type: RaidType,
        difficulty: Difficulty,
        gateNumber: Int,
        failAction:(e:Exception) -> Unit ={},
        successAction: () -> Unit
    ) {
        val data = HashMap<String, Any>()
        data["title"] = "test"
        data["type"] = type.toString()
        data["difficulty"] = difficulty.toString()
        data["gateNumber"] = gateNumber
        data["isFinish"] = false
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .document().set(data)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener { e->
                failAction(e)
            }
    }

    fun addCharacters(
        characterList: List<CharacterInfo>
    ) {
        characterList.forEach { addCharacter(it) }
    }
    private fun addCharacter(
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

    fun observeUsers(
        roomId: String,
        db:AppDataBase
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("users")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("JWH", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.i("JWH", "Listen Users")
                    // 현재 방에 있는 유저 목록 가져옴
                    CoroutineScope(Dispatchers.IO).launch {
                        val dbUserList = db.getUsersByRoomIdValue(roomId).map { it.name }.toMutableSet()
                        // 이름 조회..
                        withContext(Dispatchers.IO) {
                            value.documents.forEach {
                                val userName = it.id
                                Log.i("JWH", "Listen Users == $userName")

                                val representativeCharacter =
                                    it.data!!["representativeCharacter"] as String
                                val characterList = it.data!!["characterList"] as List<String>

                                //1 캐릭터 정보 업데이트
                                launch {
                                    characterList.forEach {characterName ->
                                        observeCharacters(characterName) {className,level,server ->
                                            db.updateCharacter(characterName, server, className, level)
                                        }
                                    }
                                }
                                //2. 유저정보 업데이트
                                launch {
                                    //이미 값이 있는 경우
                                    if (userName in dbUserList) {
                                        //업데이트
                                        db.updateUserInfo(
                                            userName,
                                            characterList,
                                            roomId,
                                            representativeCharacter
                                        )
                                        dbUserList.remove(userName)
                                    } else {
                                        //없는 경우 추가
                                        db.addUser(
                                            userName,
                                            roomId,
                                            representativeCharacter,
                                            characterList
                                        )
                                    }
                                }
                            }
                        }

                        // 동작이 끝난후 남아있다면
                        dbUserList.forEach { name ->
                            db.deleteUserName(name, roomId)
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
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