package com.wonddak.loacell.store

import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomInfoField
import dev.zacsweers.metro.Inject

/** Firestore 방 문서 접근을 한곳에 모은 데이터 계층 구현체. */
@Inject
class RoomRepository(
    private val fireStore: CommonFireStore,
) {
    fun observeAll(
        userId: String,
        successAction: (List<RoomInfo>) -> Unit,
    ): CommonListenerRegistration = rooms()
        .where(
            CommonFilter.or(
                CommonFilter.equalTo(RoomInfoField.OWNER, userId),
                CommonFilter.arrayContains(RoomInfoField.EDITABLE_USER, userId),
                CommonFilter.arrayContains(RoomInfoField.ENTER_USER, userId),
            )
        )
        .getListenerRegistration(
            successAction = { documents -> successAction(documents.map { it.toRoomInfo() }) },
            failAction = {},
        )

    fun create(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: () -> Unit,
    ) {
        rooms().document().let { room ->
            room.set(
                data = mapOf(
                    RoomInfoField.TITLE to title,
                    RoomInfoField.DESCRIPTION to description,
                    RoomInfoField.OWNER to owner,
                    RoomInfoField.PASSWORD to password,
                ),
                successAction = successAction,
                failAction = {},
            )
        }
    }

    fun get(roomId: String, onResult: (RoomInfo?) -> Unit) {
        room(roomId).get(
            successAction = { snapshot -> onResult(snapshot.takeIf { it.exist }?.toRoomInfo()) },
            failAction = { onResult(null) },
        )
    }

    fun enter(
        roomId: String,
        userId: String,
        successAction: () -> Unit,
        failAction: () -> Unit,
    ) = room(roomId).update(
        field = RoomInfoField.ENTER_USER,
        value = CommonFieldValue.arrayUnion(userId),
        successAction = successAction,
        failAction = { failAction() },
    )

    fun exit(
        roomId: String,
        userId: String,
        role: RoomInfo.RoomRole,
        successAction: () -> Unit,
        failAction: () -> Unit,
    ) {
        val field = when (role) {
            RoomInfo.RoomRole.MANAGER -> RoomInfoField.EDITABLE_USER
            RoomInfo.RoomRole.USER -> RoomInfoField.ENTER_USER
            RoomInfo.RoomRole.OWNER, RoomInfo.RoomRole.NONE -> return
        }
        room(roomId).update(
            field = field,
            value = CommonFieldValue.arrayRemove(userId),
            successAction = successAction,
            failAction = { failAction() },
        )
    }

    fun removeUsers(
        roomId: String,
        userIds: List<String>,
        field: String,
        completed: () -> Unit,
    ) = fireStore.runBatch(
        write = { batch ->
            val room = room(roomId)
            userIds.forEach { userId ->
                batch.update(room, field, CommonFieldValue.arrayRemove(userId))
            }
        },
        successAction = completed,
        failAction = { completed() },
    )

    private fun rooms(): CommonCollection = fireStore.collection("rooms")

    private fun room(roomId: String): CommonDocument = rooms().document(roomId)
}

private fun CommonDocumentSnapshot.toRoomInfo(): RoomInfo = with(requireNotNull(data)) {
    RoomInfo(
        uniqueId = this@toRoomInfo.id,
        title = this[RoomInfoField.TITLE] as String,
        description = this[RoomInfoField.DESCRIPTION] as String,
        owner = this[RoomInfoField.OWNER] as String,
        enterPassword = this[RoomInfoField.PASSWORD] as String,
        enterUser = (this[RoomInfoField.ENTER_USER] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        editableUser = (this[RoomInfoField.EDITABLE_USER] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    )
}
