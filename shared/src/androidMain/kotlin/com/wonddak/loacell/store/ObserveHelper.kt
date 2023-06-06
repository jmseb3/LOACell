package com.wonddak.loacell.store

import android.util.Log
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.ext.convertDifficulty
import com.wonddak.loacell.ext.convertType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class ListenerDoc(val android: ListenerRegistration) {
    actual fun remove() = android.remove()
}

actual object ObserveHelper {
    actual fun roomInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = Firebase.firestore.collection("rooms")
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
        return ListenerDoc(ref)
    }

    actual fun users(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = Firebase.firestore.collection("rooms")
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
                        value.documents.forEach {
                            val userName = it.id

                            val representativeCharacter =
                                it.data!!["representativeCharacter"] as String
                            val characterNameList =
                                it.data!!["characterList"] as List<String>
                            val timeStamp = it.data!!["timeStamp"] as Long
                            Log.i("JWH", "Listen Users == $userName")
                            Log.i("JWH", characterNameList.joinToString("|"))

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

                        // 동작이 끝난후 남아있다면
                        dbUserList.forEach { name ->
                            db.userInfoQueriesHelper.deleteUserName(name, roomId)
                        }
                    }
                } else {
                    Log.d("JWH", "Current data: null")
                }
            }
        return ListenerDoc(ref)
    }

    actual fun raidInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = Firebase.firestore.collection("rooms")
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
                                val isFinish = it.data!!["finish"] as Boolean
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
        return ListenerDoc(ref)
    }
}