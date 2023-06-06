package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRDocumentSnapshot
import cocoapods.FirebaseFirestore.FIRFirestore
import cocoapods.FirebaseFirestore.FIRListenerRegistrationProtocol
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.ext.convertDifficulty
import com.wonddak.loacell.ext.convertType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


actual class ListenerDoc(val ios: FIRListenerRegistrationProtocol) {
    actual fun remove() = ios.remove()
}

actual object ObserveHelper {
    actual fun roomInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = FIRFirestore.firestore().collectionWithPath("rooms")
            .documentWithPath(roomId)
            .addSnapshotListener { firDocumentSnapshot, nsError ->
                if (nsError != null) {
                    println("JWH Listen failed. $nsError")
                    return@addSnapshotListener
                }

                if (firDocumentSnapshot != null) {
                    println("JWH Listen RommInfo")

                    CoroutineScope(Dispatchers.IO).launch {
                        firDocumentSnapshot.data()?.let {
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
                    println("JWH Current data: null")
                }
            }

        return ListenerDoc(ref)
    }

    actual fun users(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = FIRFirestore.firestore().collectionWithPath("rooms")
            .documentWithPath(roomId)
            .collectionWithPath("users")
            .addSnapshotListener { firQuerySnapshot, nsError ->
                if (nsError != null) {
                    println("JWH Listen failed. $nsError")
                    return@addSnapshotListener
                }

                if (firQuerySnapshot != null) {
                    println("JWH Listen Users")

                    CoroutineScope(Dispatchers.IO).launch {
                        val dbUserList =
                            db.userInfoQueriesHelper.getUsersByRoomIdValue(roomId).map { it.name }
                                .toMutableSet()
                        // 이름 조회..
                        firQuerySnapshot.documents.forEach {
                            if (it is FIRDocumentSnapshot) {
                                val userName = it.documentID
                                val representativeCharacter =
                                    it.data()!!["representativeCharacter"] as String
                                val characterNameList =
                                    it.data()!!["characterList"] as List<String>
                                val timeStamp = it.data()!!["timeStamp"] as Long
                                println("JWH Listen Users == $userName")
                                println("JWH ${characterNameList.joinToString("|")}")

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


                        // 동작이 끝난후 남아있다면
                        dbUserList.forEach { name ->
                            db.userInfoQueriesHelper.deleteUserName(name, roomId)
                        }
                    }

                } else {
                    println("JWH Current data: null")
                }
            }
        return ListenerDoc(ref)
    }

    actual fun raidInfo(
        roomId: String,
        db: AppDataBase
    ): ListenerDoc {
        val ref = FIRFirestore.firestore().collectionWithPath("rooms")
            .documentWithPath(roomId)
            .collectionWithPath("raidInfo")
            .addSnapshotListener { firQuerySnapshot, nsError ->
                if (nsError != null) {
                    println("JWH Listen failed. $nsError")
                    return@addSnapshotListener
                }

                if (firQuerySnapshot != null) {
                    println("JWH Listen raidInfo")
                    // 현재 있는 레이드 정보를 가져옴
                    CoroutineScope(Dispatchers.IO).launch {
                        val dbRaidList =
                            db.raidInfoQueriesHelper.getAllByRoomIdValue(roomId).map { it.raidId }
                                .toMutableSet()
                        println("JWH $dbRaidList")
                        // 레이드 id 조회..
                        withContext(Dispatchers.IO) {
                            firQuerySnapshot.documents.forEach {
                                if (it is FIRDocumentSnapshot) {
                                    val raidId = it.documentID
                                    println("JWH Listen raidId : $raidId")
                                    val title = it.data()!!["title"] as String
                                    val typeString = it.data()!!["type"] as String
                                    val difficultyString = it.data()!!["difficulty"] as String
                                    val startGateNumber = it.data()!!["startGateNumber"] as Long
                                    val endGateNumber = it.data()!!["endGateNumber"] as Long
                                    val isFinish = it.data()!!["isFinish"] as Boolean
                                    val party1 = it.data()!!["party1"] as List<String>
                                    val party2 = it.data()!!["party2"] as List<String>

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
                        }

                        // 동작이 끝난후 남아있다면
                        dbRaidList.forEach { name ->
                            db.raidInfoQueriesHelper.delete(name, roomId)
                        }
                    }

                } else {
                    println("JWH Current data: null")
                }
            }
        return ListenerDoc(ref)
    }
}