package com.wonddak.loacell.android.util

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.api.model.CharacterInfo
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.database.const.Difficulty
import com.wonddak.loacell.database.const.RaidType
import com.wonddak.loacell.database.const.convertDifficulty
import com.wonddak.loacell.database.const.convertType
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
        startGateNumber: Int,
        endGateNumber: Int,
        failAction:(e:Exception) -> Unit ={},
        successAction: () -> Unit
    ) {
        val data = HashMap<String, Any>()
        data["title"] = "test"
        data["type"] = type.name
        data["difficulty"] = difficulty.name
        data["startGateNumber"] = startGateNumber
        data["endGateNumber"] = endGateNumber
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
                        val dbUserList = db.userInfoQueriesHelper.getUsersByRoomIdValue(roomId).map { it.name }.toMutableSet()
                        // 이름 조회..
                        withContext(Dispatchers.IO) {
                            value.documents.forEach {
                                val userName = it.id

                                val representativeCharacter =
                                    it.data!!["representativeCharacter"] as String
                                val characterList = it.data!!["characterList"] as List<String>
                                Log.i("JWH", "Listen Users == $userName")
                                Log.i("JWH", characterList.joinToString("|"))

                                //1 캐릭터 정보 업데이트
                                launch {
                                    characterList.forEach {characterName ->
                                        observeCharacters(characterName) {className,level,server ->
                                            db.characterInfoQueriesHelper.updateCharacter(characterName, server, className, level)
                                        }
                                    }
                                }
                                //2. 유저정보 업데이트
                                launch {
                                    //이미 값이 있는 경우
                                    if (userName in dbUserList) {
                                        //업데이트
                                        db.userInfoQueriesHelper.updateUserInfo(
                                            userName,
                                            characterList,
                                            roomId,
                                            representativeCharacter
                                        )
                                        dbUserList.remove(userName)
                                    } else {
                                        //없는 경우 추가
                                        db.userInfoQueriesHelper.addUser(
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
                            db.userInfoQueriesHelper.deleteUserName(name, roomId)
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
                }
            }
    }

    fun observeRoomInfo(
        roomId: String,
        db:AppDataBase
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("JWH", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.i("JWH", "Listen RoomInfo")

                    CoroutineScope(Dispatchers.IO).launch {
                        value.data?.let {
                            val title = it["title"] as String
                            val description = it["description"] as String
                            db.roomInfoQueriesHelper.updateRoomInfo(title,description,roomId)
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
                    Log.w("JWH", "Characters Listen failed.", error)
                    return@addSnapshotListener
                }
                if (value != null) {
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

    fun observeRaid(
        roomId: String,
        db: AppDataBase
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("JWH", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    Log.i("JWH", "Listen raidInfo")
                    // 현재 있는 레이드 정보를 가져옴
                    CoroutineScope(Dispatchers.IO).launch {
                        val dbRaidList = db.raidInfoQueriesHelper.getAllByRoomIdValue(roomId).map { it.raidId }.toMutableSet()
                        Log.i("JWH",dbRaidList.toString())
                        // 레이드 id 조회..
                        withContext(Dispatchers.IO) {
                            value.documents.forEach {
                                val raidId = it.id
                                Log.i("JWH", "Listen raidId : $raidId")

                                val title = it.data!!["title"] as String
                                val typeString = it.data!!["type"] as String
                                val difficultyString = it.data!!["difficulty"] as String
                                val startGateNumber = it.data!!["startGateNumber"] as Long
                                val endGateNumber = it.data!!["endGateNumber"] as Long
                                val isFinish = it.data!!["isFinish"] as Boolean


                                //이미 값이 있는 경우
                                if (raidId in dbRaidList) {
                                    //업데이트
                                    db.raidInfoQueriesHelper.updateRaidInfo(
                                        raidId,
                                        roomId,
                                        title,
                                        typeString.convertType(),
                                        difficultyString.convertDifficulty(),
                                        startGateNumber,
                                        endGateNumber,
                                        isFinish
                                    )
                                    dbRaidList.remove(raidId)
                                } else {
                                    //없는 경우 추가
                                    db.raidInfoQueriesHelper.addRaidInfo(
                                        raidId,
                                        roomId,
                                        title,
                                        typeString.convertType(),
                                        difficultyString.convertDifficulty(),
                                        startGateNumber,
                                        endGateNumber
                                    )
                                }

                            }
                        }

                        // 동작이 끝난후 남아있다면
                        dbRaidList.forEach { name ->
                            db.raidInfoQueriesHelper.delete(name,roomId)
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
                }
            }
    }
}