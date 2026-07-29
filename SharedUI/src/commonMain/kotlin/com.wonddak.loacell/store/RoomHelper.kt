package com.wonddak.loacell.store

import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomInfoField
import io.github.aakira.napier.Napier

private fun CommonDocumentSnapshot.toRoomInfo(): RoomInfo = with(requireNotNull(data)) {
    RoomInfo(
        uniqueId = this@toRoomInfo.id,
        title = this[RoomInfoField.TITLE] as String,
        description = this[RoomInfoField.DESCRIPTION] as String,
        owner = this[RoomInfoField.OWNER] as String,
        enterPassword = this[RoomInfoField.PASSWORD] as String,
        enterUser = this[RoomInfoField.ENTER_USER].asStringList(),
        editableUser = this[RoomInfoField.EDITABLE_USER].asStringList(),
    )
}

private fun Any?.asStringList(): List<String> =
    (this as? List<*>)?.filterIsInstance<String>() ?: emptyList()

object CommonRoomHelper {
    // 방 입장요청시 실제 존재하는 방인지 체크
    fun checkExist(
        roomId: String,
        successAction: (roomInfo: RoomInfo) -> Unit,
        failAction: () -> Unit,
    ) {
        RefHelper.getRoomRef(roomId)
            .get(
                successAction = {
                    if (it.exist) {
                        successAction(it.toRoomInfo())
                    } else {
                        failAction()
                    }

                },
                failAction = { failAction() }
            )
    }

    // 방 생성시 인포를 넣고 id값 을 가져옴
    fun makeInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit,
    ) {
        Napier.d(tag = "Room") { "init [$title/$description/$password/$owner]" }
        val data = mapOf(
            RoomInfoField.TITLE to title,
            RoomInfoField.DESCRIPTION to description,
            RoomInfoField.OWNER to owner,
            RoomInfoField.PASSWORD to password,
        )
        val ref = RefHelper.getRoomsRef().document()
        ref.set(
            data,
            successAction = {
                Napier.d(tag = "Room") { "success init" }
                successAction(ref.id)
            },
            failAction = {
                Napier.e(tag = "Room") { "fail init $it" }
            }
        )
    }

    // 유저를 방에 추가한다.
    fun enterRoom(
        roomId: String,
        userId: String,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit,
    ) {
        RefHelper.getRoomRef(roomId).update(
            field = RoomInfoField.ENTER_USER,
            value = CommonFieldValue.arrayUnion(userId),
            successAction = successAction,
            failAction = failAction
        )
    }

    //방 정보를 업데이트 한다
    fun updateRoom(
        roomId: String,
        title: String,
        description: String,
        password: String,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit,
    ) {
        RefHelper.getRoomRef(roomId)
            .update(
                mapOf(
                    RoomInfoField.TITLE to title,
                    RoomInfoField.DESCRIPTION to description,
                    RoomInfoField.PASSWORD to password
                ),
                successAction = successAction,
                failAction = failAction
            )
    }

    fun exitUsersFromRoom(
        roomId: String,
        userId: List<String>,
        field: String,
        commonAction: () -> Unit = {},
        successAction: () -> Unit = {},
        failAction: (e: Error) -> Unit = {},
    ) {
        getFireStore().runBatch(
            write = {
                val ref = RefHelper.getRoomRef(roomId)
                userId.forEach { uid ->
                    it.update(ref, field, CommonFieldValue.arrayRemove(uid))
                }
            },
            successAction = {
                commonAction()
                successAction()
            },
            failAction = {
                commonAction()
                failAction(it)
            }
        )
    }

    fun exitEditableUserFromRoom(
        roomId: String,
        editableUser: List<String>,
        commonAction: () -> Unit,
    ) = exitUsersFromRoom(roomId, editableUser, RoomInfoField.EDITABLE_USER, commonAction)

    fun exitEnterUserFromRoom(
        roomId: String,
        enterUser: List<String>,
        commonAction: () -> Unit,
    ) = exitUsersFromRoom(roomId, enterUser, RoomInfoField.ENTER_USER, commonAction)

    fun exitRoom(
        roomId: String,
        userId: String,
        role: RoomInfo.RoomRole,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit,
    ) {
		Napier.d { "EXIT $roomId / $userId / $role" }
        val field = when (role) {
            RoomInfo.RoomRole.MANAGER -> RoomInfoField.EDITABLE_USER
            RoomInfo.RoomRole.USER -> RoomInfoField.ENTER_USER
            else -> RoomInfoField.OWNER
        }
        RefHelper.getRoomRef(roomId).update(
            field = field,
            value = CommonFieldValue.arrayRemove(userId),
            successAction = successAction,
            failAction = failAction
        )
    }

    fun observeAllRoom(
        userId: String,
        successAction: (List<RoomInfo>) -> Unit,
    ): CommonListenerRegistration {
        return RefHelper.getRoomsRef()
            .where(
                CommonFilter.or(
                    CommonFilter.equalTo(RoomInfoField.OWNER, userId),
                    CommonFilter.arrayContains(RoomInfoField.EDITABLE_USER, userId),
					CommonFilter.arrayContains(RoomInfoField.ENTER_USER, userId),
                )
            )
            .getListenerRegistration(
                successAction = { documentList ->
                    successAction(documentList.map { it.toRoomInfo() })
                },
                failAction = {}
            )
    }

    fun deleteRoom(
        roomId: String,
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit,
    ) {
        RefHelper.getRoomRef(roomId).delete(
            successAction = successAction,
            failAction = failAction
        )
    }

    fun changeOwner(
        roomId: String,
        preOwner: String,
        newOwnerUid: String,
        commonAction: () -> Unit = {},
        successAction: () -> Unit,
        failAction: (e: Error) -> Unit,
    ) {
        getFireStore().runBatch(
            write = {
                val roomDoc = RefHelper.getRoomRef(roomId)
                it.update(roomDoc, RoomInfoField.ENTER_USER, CommonFieldValue.arrayUnion(preOwner))
                it.update(roomDoc, RoomInfoField.OWNER, newOwnerUid)
                it.update(roomDoc, RoomInfoField.ENTER_USER, CommonFieldValue.arrayRemove(newOwnerUid))
            },
            successAction = {
                commonAction()
                successAction()
            },
            failAction = {
                commonAction()
                failAction(it)
            }
        )
    }
}
