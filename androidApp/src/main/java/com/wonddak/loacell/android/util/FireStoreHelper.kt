package com.wonddak.loacell.android.util

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.ext.convertDifficulty
import com.wonddak.loacell.ext.convertType
import com.wonddak.loacell.model.Difficulty
import com.wonddak.loacell.model.RaidType
import com.wonddak.sharedapi.model.CharacterInfo
import com.wonddak.loacell.store.RoomHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FireStoreHelper {
    fun syncRoomInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error :String) -> Unit,
        successAction: () -> Unit
    ) {
        RoomHelper.syncInfo(
            userId,
            successAction = successAction,
            db = db,
            failAction = failAction
        )
    }

    fun checkExistRoomInfo(
        roomId: String,
        successAction: (password:String) -> Unit,
        failAction: () -> Unit,
    ) {
        Firebase.firestore.collection("rooms").document(roomId).let { roomRef ->
            roomRef.get()
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
    }

    //region room
    fun addRoomInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit
    ) {
        Firebase.firestore.let { fs ->
            val data = HashMap<String, Any>()
            data["title"] = title
            data["description"] = description
            data["owner"] = owner
            data["editableUser"] = emptyList<String>()
            data["enterUser"] = emptyList<String>()
            data["anonymousUser"] = emptyList<String>()
            data["enterPassword"] = password
            val newRooms = fs.collection("rooms").document()
            newRooms.set(data).addOnSuccessListener {
                successAction(newRooms.id)
            }
        }
    }

    fun addUserToRoom(
        roomId: String,
        userId: String,
        db :AppDataBase,
        successAction: () -> Unit,
        failAction: (e :Exception?) -> Unit,
    ) {
        Firebase.firestore.collection("rooms").document(roomId).let { roomRef ->
            roomRef.update("enterUser", FieldValue.arrayUnion(userId))
                .addOnSuccessListener {
                    roomRef.get().addOnSuccessListener { document ->
                        val title = document.data!!["title"] as String
                        val description = document.data!!["description"] as String
                        val owner = document.data!!["owner"] as String

                        Log.d("JWH", "${document.id} => ${document.data}")

                        db.roomInfoQueriesHelper.addRoomInfo(
                            title = title,
                            description = description,
                            uniqueId = roomId,
                            owner = owner
                        )
                        successAction()
                    }.addOnFailureListener {
                        failAction(it)
                    }

                }
                .addOnFailureListener {
                    it.printStackTrace()
                    failAction(it)
                }
        }
    }
    //endregion

    //region User
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
            userData["characterList"] = characterList.map {
                Firebase.firestore.collection("characters").document(it.characterName)
            }
            userData["timeStamp"] = System.currentTimeMillis()
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
        failAction: (e: Exception) -> Unit,
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
            .addOnFailureListener { e ->
                failAction(e)
            }
    }
    //endregion

    //region Raid
    fun addRaidInfo(
        roomId: String,
        title: String,
        type: RaidType,
        difficulty: Difficulty,
        startGateNumber: Int,
        endGateNumber: Int,
        failAction: (e: Exception) -> Unit = {},
        successAction: () -> Unit
    ) {
        val data = HashMap<String, Any>()
        data["title"] = title
        data["type"] = type.name
        data["difficulty"] = difficulty.name
        data["startGateNumber"] = startGateNumber
        data["endGateNumber"] = endGateNumber
        data["isFinish"] = false
        data["party1"] = listOf("", "", "", "")
        data["party2"] = listOf("", "", "", "")
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .document().set(data)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener { e ->
                failAction(e)
            }
    }

    fun updateRaidUser(
        roomId: String,
        raidId: String,
        partyIndex: Int,
        partyList: List<String>,
        successAction: () -> Unit
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .document(raidId).update("party$partyIndex", partyList)
            .addOnSuccessListener {
                successAction()
            }
    }

    fun deleteRaidInfo(
        roomId: String,
        raidId: String,
        failAction: (e: Exception) -> Unit,
        successAction: () -> Unit
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .document(raidId)
            .delete()
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener { e ->
                failAction(e)
            }
    }

    fun deleteRaidUserInfo(
        roomId: String,
        raidId: String,
        partyIndex: Int,
        partyList: List<String>,
        failAction: (e: Exception) -> Unit,
        successAction: () -> Unit
    ) {
        Firebase.firestore.collection("rooms")
            .document(roomId)
            .collection("raidInfo")
            .document(raidId)
            .update("party$partyIndex", partyList)
            .addOnSuccessListener {
                successAction()
            }
            .addOnFailureListener { e ->
                failAction(e)
            }
    }

    //endregion
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
                            // 30분 이상 지나야 갱신 허용
                            if (lastTime + 1_800L * 1_000L >= nowTime) {
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

    //현재 들어간 roomId의 정보를 갱신
    fun observeRoomInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerRegistration {
        return Firebase.firestore.collection("rooms")
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
                            val owner = it["owner"] as String
                            db.roomInfoQueriesHelper.updateRoomInfo(
                                title,
                                description,
                                owner,
                                roomId
                            )
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
                }
            }
    }

    //현재 들어간 roomId의 유저 정보들을 갱신
    fun observeUsers(
        roomId: String,
        db: AppDataBase
    ): ListenerRegistration {
        return Firebase.firestore.collection("rooms")
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
                        val dbUserList =
                            db.userInfoQueriesHelper.getUsersByRoomIdValue(roomId).map { it.name }
                                .toMutableSet()
                        // 이름 조회..
                        withContext(Dispatchers.IO) {
                            value.documents.forEach {
                                val userName = it.id

                                val representativeCharacter =
                                    it.data!!["representativeCharacter"] as String
                                val characterList =
                                    it.data!!["characterList"] as List<DocumentReference>
                                val characterNameList = characterList.map { it.id }
                                val timeStamp = it.data!!["timeStamp"] as Long
                                Log.i("JWH", "Listen Users == $userName")
                                Log.i("JWH", characterNameList.joinToString("|"))
                                launch {
                                    characterList.forEach { documentReference ->
                                        val characterName = documentReference.id
                                        documentReference.get().addOnSuccessListener { doc ->
                                            if (doc != null) {
                                                doc.data?.let { data ->
                                                    val className = data["className"] as String
                                                    val level = data["level"] as String
                                                    val server = data["server"] as String
                                                    Log.d(
                                                        "JWH",
                                                        "Listen characters value :$characterName"
                                                    )
                                                    db.characterInfoQueriesHelper.updateCharacter(
                                                        characterName,
                                                        server,
                                                        className,
                                                        level
                                                    )
                                                }
                                            } else {
                                                Log.d("JWH", "Listen characters value null")
                                            }
                                        }
                                    }
                                }
                                launch {
                                    //이미 값이 있는 경우
                                    if (userName in dbUserList) {
                                        //업데이트
                                        db.userInfoQueriesHelper.updateUserInfo(
                                            userName,
                                            characterNameList,
                                            roomId,
                                            representativeCharacter,
                                            timeStamp
                                        )
                                        dbUserList.remove(userName)
                                    } else {
                                        //없는 경우 추가
                                        db.userInfoQueriesHelper.addUser(
                                            userName,
                                            roomId,
                                            representativeCharacter,
                                            characterNameList,
                                            timeStamp
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

    //현재 들어간 roomId의 레이드 정보들을 갱신
    fun observeRaid(
        roomId: String,
        db: AppDataBase
    ): ListenerRegistration {
        return Firebase.firestore.collection("rooms")
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
                        val dbRaidList =
                            db.raidInfoQueriesHelper.getAllByRoomIdValue(roomId).map { it.raidId }
                                .toMutableSet()
                        Log.i("JWH", dbRaidList.toString())
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
                                val party1 = it.data!!["party1"] as List<String>
                                val party2 = it.data!!["party2"] as List<String>

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
                                        isFinish,
                                        party1,
                                        party2
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
                                        endGateNumber,
                                        party1,
                                        party2
                                    )
                                }

                            }
                        }

                        // 동작이 끝난후 남아있다면
                        dbRaidList.forEach { name ->
                            db.raidInfoQueriesHelper.delete(name, roomId)
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
                }
            }
    }
}