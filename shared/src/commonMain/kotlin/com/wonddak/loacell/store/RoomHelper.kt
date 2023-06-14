package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RoomRole

data class FBRoomInfo(
    val title: String = "",
    val description: String = "",
    val owner: String = "",
    val enterPassword: String = "",
    val anonymousUser: List<String> = emptyList(),
    val editableUser: List<String> = emptyList(),
    val enterUser: List<String> = emptyList(),
) {
    fun toMap(): Map<String, Any> = mapOf(
        "title" to title,
        "description" to description,
        "owner" to owner,
        "enterPassword" to enterPassword,
        "anonymousUser" to anonymousUser,
        "editableUser" to editableUser,
        "enterUser" to enterUser,
    )
}
object CommonRoomHelper {

    //로그인시 동기화를 위한 메서드
    fun syncInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error: Error) -> Unit,
        successAction: () -> Unit
    ) {
        RefHelper.getRoomsRef()
            .where(
                CommonFilter.or(
                    CommonFilter.equalTo("owner", userId),
                    CommonFilter.arrayContains("anonymousUser", userId),
                    CommonFilter.arrayContains("editableUser", userId),
                    CommonFilter.arrayContains("enterUser", userId),
                )
            ).get(
                successAction = { querySnapshot ->
                    querySnapshot.documents.forEach { document ->
                        val id = document.id
                        val data = document.data!!
                        val title = data["title"] as String
                        val description = data["description"] as String
                        val owner = data["owner"] as String
                        val enterUser = data["enterUser"] as List<String>
                        val editableUser = data["editableUser"] as List<String>
                        val anonymousUser = data["anonymousUser"] as List<String>
                        db.roomInfoQueriesHelper.addRoomInfo(
                            title = title,
                            description = description,
                            uniqueId = id,
                            owner = owner,
                            enterUser = enterUser,
                            editableUser = editableUser,
                            anonymousUser = anonymousUser
                        )
                    }
                    successAction()
                },
                failAction = failAction
            )

    }

    // 방 입장요청시 실제 존재하는 방인지 체크
    fun checkExist(
        roomId: String,
        successAction: (roomInfo :FBRoomInfo) -> Unit,
        failAction: () -> Unit
    ) {
        RefHelper.getRoomRef(roomId)
            .get(
                successAction = {
                    if (it.exist) {
                        val roomInfo = FBRoomInfo(
                            title = it.data!!["title"] as String,
                            description = it.data!!["description"] as String,
                            owner = it.data!!["owner"] as String,
                            enterPassword = it.data!!["enterPassword"] as String,
                            anonymousUser = it.data!!["anonymousUser"] as List<String>,
                            editableUser = it.data!!["editableUser"] as List<String>,
                            enterUser = it.data!!["enterUser"] as List<String>
                        )
                        successAction(roomInfo)
                    } else {
                        failAction()
                    }

                },
                failAction = {failAction()}
            )
    }

    // 방 생성시 인포를 넣고 id값 을 가져옴
    fun makeInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit
    ) {
        val data = FBRoomInfo(
            title = title,
            description = description,
            enterPassword = password,
            owner = owner
        )
        val ref = RefHelper.getRoomsRef().document()
        ref.set(
            data.toMap(),
            successAction = {
                successAction(ref.id)
            },
            failAction = {

            }
        )
    }

    // 유저를 방에 추가한다.
    fun enterRoom(
        roomId: String,
        userId: String,
        isAnonymous: Boolean,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit
    ) {
        val field = if (isAnonymous) "anonymousUser" else "enterUser"
        RefHelper.getRoomRef(roomId).update(
            field = field,
            value = CommonFieldValue.arrayUnion(userId),
            successAction = successAction,
            failAction = failAction
        )
    }
    fun exitUsersFromRoom(
        roomId: String,
        userId: List<String>,
        field: String,
        commonAction:() ->Unit={},
        successAction: () -> Unit ={},
        failAction: (e: Error) -> Unit={}
    ) {
        getFireStore().runTransaction(
            refDoc =  RefHelper.getRoomRef(roomId),
            successAction ={
                commonAction()
                successAction()
            },
            failAction = {
                commonAction()
                failAction(it)
            }
        ) {
            userId.forEach { uid ->
                it.reference.update(
                    field = field,
                    value = CommonFieldValue.arrayRemove(uid),
                    successAction = { },
                    failAction = { }
                )
            }
        }
    }

    fun exitRoom(
        roomId: String,
        userId: String,
        role: RoomRole,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit
    ) {
        val field = when(role) {
            RoomRole.MANAGER -> "editableUser"
            RoomRole.USER -> "enterUser"
            RoomRole.ANONYMOUS -> "anonymousUser"
            else -> "owner"
        }
        RefHelper.getRoomRef(roomId).update(
            field = field,
            value = CommonFieldValue.arrayRemove(userId),
            successAction = successAction,
            failAction = failAction
        )
    }

    fun observe(
        roomId: String,
        db:AppDataBase
    ) : CommonListenerRegistration {
        return RefHelper.getRoomRef(roomId).getListenerRegistration(
            successAction =  {
                it.data?.let { data ->
                    val title = data["title"] as String
                    val description = data["description"] as String
                    val owner = data["owner"] as String
                    val enterUser = data["enterUser"] as List<String>
                    val editableUser = data["editableUser"] as List<String>
                    val anonymousUser = data["anonymousUser"] as List<String>
                    db.roomInfoQueriesHelper.updateRoomInfo(
                        title,
                        description,
                        owner,
                        enterUser,
                        editableUser,
                        anonymousUser,
                        roomId
                    )
                }
            },
            failAction = {
                println("JWH Fail with error : ${it?.errorMsg}")
            }
        )
    }
}