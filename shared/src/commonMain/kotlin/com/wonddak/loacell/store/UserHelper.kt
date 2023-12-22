package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.lostark.model.CharacterInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class FBUSerInfo(
    val representativeCharacter: String = "",
    val characterList: List<FBCharacterInfo> = emptyList(),
    val timeStamp: Long = Clock.System.now().toEpochMilliseconds()
) {
    fun toMap() = mapOf(
        "representativeCharacter" to representativeCharacter,
        "characterList" to characterList.map { it.toMap() },
        "timeStamp" to timeStamp
    )
}

data class FBCharacterInfo(
    val name: String = "",
    val server: String = "",
    val className: String = "",
    val level: String = ""
) {
    fun toMap() = mapOf<String, Any>(
        "name" to name,
        "server" to server,
        "className" to className,
        "level" to level
    )
}

object CommonUserHelper {

    // 방에 유저정보를 추가한다.
    fun addOrUpdate(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        val fbUserInfo = FBUSerInfo(
            representativeCharacter,
            characterList.map {
                FBCharacterInfo(
                    it.characterName,
                    it.serverName,
                    it.characterClassName,
                    it.itemMaxLevel
                )
            }
        )
        val userRoom = RefHelper.getUserDocRef(roomId, name)

        userRoom.get(
            successAction = {
                if (it.exist) {
                    userRoom.update(
                        data = fbUserInfo.toMap(),
                        successAction = successAction,
                        failAction = { err ->
                            failAction(err.errorMsg)
                        }
                    )
                } else {
                    runCatching {
                        userRoom.set(
                            data = fbUserInfo.toMap(),
                            successAction = successAction,
                            failAction = { err ->
                                failAction(err.errorMsg)
                            }
                        )
                    }.onFailure {e ->
                        failAction(e.message ?:"dead")
                    }
                }
            },
            failAction = {
                failAction(it.errorMsg)
            }
        )

    }

    // 유저의 대표 캐릭터를 변경한다.
    fun updateRepresentativeCharacter(
        roomId: String,
        name: String,
        representativeCharacter: String
    ) {
        RefHelper.getUserDocRef(roomId, name)
            .update("representativeCharacter", representativeCharacter)
    }

    //유저 정보를 삭제한다.
    fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        println("--USERHELPER DELETE!!")
        RefHelper.getUserDocRef(roomId, name)
            .delete(
                successAction = successAction,
                failAction = { failAction(it.errorMsg) }
            )
    }

    fun observe(
        roomId: String,
        db: AppDataBase
    ): CommonListenerRegistration {
        return RefHelper.getUsersRef(roomId).getListenerRegistration(
            successAction = { value ->
                val dbUserList =
                    db.userInfoQueriesHelper.getUsersByRoomIdValue(roomId).map { it.name }
                        .toMutableSet()
                // 이름 조회..
                CoroutineScope(Dispatchers.IO).launch {
                    value.documents.forEach {
                        val userName = it.id

                        val representativeCharacter =
                            it.data!!["representativeCharacter"] as String
                        val getCharacterList =
                            it.data!!["characterList"] as List<Map<String, Any>>

                        val characterList = getCharacterList.map {
                            FBCharacterInfo(
                                it["name"] as String,
                                it["server"] as String,
                                it["className"] as String,
                                it["level"] as String
                            )
                        }

                        val timeStamp = it.data!!["timeStamp"] as Long
                        //이미 값이 있는 경우
                        if (userName in dbUserList) {
                            //업데이트
                            db.userInfoQueriesHelper.updateUserInfo(
                                userName,
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
                                timeStamp
                            )
                        }
                        characterList.forEach {
                            db.characterQueriesHelper.insertCharacter(
                                userName,
                                roomId,
                                it.name,
                                it.server,
                                it.className,
                                it.level
                            )

                        }
                    }
                    // 동작이 끝난후 남아있다면
                    dbUserList.forEach { name ->
                        db.userInfoQueriesHelper.deleteUserName(name, roomId)
                    }
                }
            },
            failAction = {

            }
        )
    }
}