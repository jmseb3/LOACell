package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.model.CharacterInfo
import korlibs.time.DateTime

data class FBUSerInfo(
    val representativeCharacter: String = "",
    val characterList: List<String> = emptyList(),
    val timeStamp: Long = DateTime.now().milliseconds.toLong(),
) {
    fun toMap() = mapOf<String, Any>(
        "representativeCharacter" to representativeCharacter,
        "characterList" to characterList,
        "timeStamp" to timeStamp
    )
}
object CommonUserHelper {

    private fun getUsersRef(
        roomId: String,
    ): CommonCollection =
        getFireStore().collection("rooms").document(roomId).collection("users")
    private fun getUserDocRef(
        roomId: String,
        name: String
    ): CommonDocument = getUsersRef(roomId).document(name)


    // 방에 유저정보를 추가한다.
    fun add(
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

        userRoom.get(
            successAction = {
                if (it.exist) {
                    failAction("이미 존재하는 이름입니다.")
                } else {
                    userRoom.set(
                        data = fbUserInfo.toMap(),
                        successAction = successAction,
                        failAction = {err ->
                            failAction(err.errorMsg)
                        }
                    )
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
        getUserDocRef(roomId,name)
            .update("representativeCharacter",representativeCharacter)
    }

    //유저 정보를 삭제한다.
    fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    ) {
        getUserDocRef(roomId,name)
            .delete(
                successAction = successAction,
                failAction = {failAction(it.errorMsg)}
            )
    }

    fun observe(
        roomId: String,
        db: AppDataBase
    ) : CommonListenerRegistration {
        return getUsersRef(roomId).getListenerRegistration(
            successAction = {value ->
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

                // 동작이 끝난후 남아있다면
                dbUserList.forEach { name ->
                    db.userInfoQueriesHelper.deleteUserName(name, roomId)
                }
            },
            failAction = {
                println("JWH Fail with error : ${it?.errorMsg}")
            }
        )
    }
}