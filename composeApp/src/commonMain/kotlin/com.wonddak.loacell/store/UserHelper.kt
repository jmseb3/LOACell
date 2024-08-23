import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.model.toUserInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.RefHelper

//package com.wonddak.loacell.store
//
//import com.wonddak.loacell.sharedapi.lostark.model.CharacterInfo
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.IO
//import kotlinx.coroutines.launch
//import kotlinx.datetime.Clock
//
//data class FBUSerInfo(
//    val representativeCharacter: String = "",
//    val characterList: List<FBCharacterInfo> = emptyList(),
//    val timeStamp: Long = Clock.System.now().toEpochMilliseconds()
//) {
//    fun toMap() = mapOf(
//        "representativeCharacter" to representativeCharacter,
//        "characterList" to characterList.map { it.toMap() },
//        "timeStamp" to timeStamp
//    )
//}
//
//data class FBCharacterInfo(
//    val name: String = "",
//    val server: String = "",
//    val className: String = "",
//    val level: String = ""
//) {
//    fun toMap() = mapOf<String, Any>(
//        "name" to name,
//        "server" to server,
//        "className" to className,
//        "level" to level
//    )
//}
//
object CommonUserHelper {
//
//    // 방에 유저정보를 추가한다.
//    fun addOrUpdate(
//        roomId: String,
//        name: String,
//        representativeCharacter: String,
//        characterList: List<CharacterInfo>,
//        failAction: (e: String) -> Unit,
//        successAction: () -> Unit
//    ) {
//        val fbUserInfo = FBUSerInfo(
//            representativeCharacter,
//            characterList.map {
//                FBCharacterInfo(
//                    it.characterName,
//                    it.serverName,
//                    it.characterClassName,
//                    it.itemMaxLevel
//                )
//            }
//        )
//        val userRoom = RefHelper.getUserDocRef(roomId, name)
//
//        userRoom.get(
//            successAction = {
//                if (it.exist) {
//                    userRoom.update(
//                        data = fbUserInfo.toMap(),
//                        successAction = successAction,
//                        failAction = { err ->
//                            failAction(err.errorMsg)
//                        }
//                    )
//                } else {
//                    runCatching {
//                        userRoom.set(
//                            data = fbUserInfo.toMap(),
//                            successAction = successAction,
//                            failAction = { err ->
//                                failAction(err.errorMsg)
//                            }
//                        )
//                    }.onFailure {e ->
//                        failAction(e.message ?:"dead")
//                    }
//                }
//            },
//            failAction = {
//                failAction(it.errorMsg)
//            }
//        )
//
//    }
//
//    // 유저의 대표 캐릭터를 변경한다.
//    fun updateRepresentativeCharacter(
//        roomId: String,
//        name: String,
//        representativeCharacter: String
//    ) {
//        RefHelper.getUserDocRef(roomId, name)
//            .update("representativeCharacter", representativeCharacter)
//    }
//
//    //유저 정보를 삭제한다.
//    fun delete(
//        roomId: String,
//        name: String,
//        failAction: (e: String) -> Unit,
//        successAction: () -> Unit
//    ) {
//        println("--USERHELPER DELETE!!")
//        RefHelper.getUserDocRef(roomId, name)
//            .delete(
//                successAction = successAction,
//                failAction = { failAction(it.errorMsg) }
//            )
//    }
//
fun observe(
    roomId: String,
    successAction: (List<UserInfo>) -> Unit,
): CommonListenerRegistration {
    return RefHelper.getUsersRef(roomId).getListenerRegistration(
        successAction = { value ->
            successAction(value.documents.map { it.toUserInfo(roomId) })
        },
        failAction = {

        }
    )
}
}