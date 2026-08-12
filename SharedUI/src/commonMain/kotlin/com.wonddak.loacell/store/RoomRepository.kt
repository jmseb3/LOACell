package com.wonddak.loacell.store

import com.wonddak.loacell.model.RoomInfo
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
                CommonFilter.equalTo(RoomDocumentField.OWNER, userId),
                CommonFilter.arrayContains(RoomDocumentField.EDITABLE_USER, userId),
                CommonFilter.arrayContains(RoomDocumentField.ENTER_USER, userId),
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
                    RoomDocumentField.TITLE to title,
                    RoomDocumentField.DESCRIPTION to description,
                    RoomDocumentField.OWNER to owner,
                    RoomDocumentField.PASSWORD to password,
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
        field = RoomDocumentField.ENTER_USER,
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
            RoomInfo.RoomRole.MANAGER -> RoomDocumentField.EDITABLE_USER
            RoomInfo.RoomRole.USER -> RoomDocumentField.ENTER_USER
            RoomInfo.RoomRole.OWNER, RoomInfo.RoomRole.NONE -> return
        }
        room(roomId).update(
            field = field,
            value = CommonFieldValue.arrayRemove(userId),
            successAction = successAction,
            failAction = { failAction() },
        )
    }

    private fun removeUsers(
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

    fun removeEditableUsers(roomId: String, userIds: List<String>, completed: () -> Unit) =
        removeUsers(roomId, userIds, RoomDocumentField.EDITABLE_USER, completed)

    fun removeEnteredUsers(roomId: String, userIds: List<String>, completed: () -> Unit) =
        removeUsers(roomId, userIds, RoomDocumentField.ENTER_USER, completed)

    private fun rooms(): CommonCollection = fireStore.collection("rooms")

    private fun room(roomId: String): CommonDocument = rooms().document(roomId)
}

private fun CommonDocumentSnapshot.toRoomInfo(): RoomInfo = with(requireNotNull(data)) {
    RoomInfo(
        uniqueId = this@toRoomInfo.id,
        title = this[RoomDocumentField.TITLE] as String,
        description = this[RoomDocumentField.DESCRIPTION] as String,
        owner = this[RoomDocumentField.OWNER] as String,
        enterPassword = this[RoomDocumentField.PASSWORD] as String,
        enterUser = (this[RoomDocumentField.ENTER_USER] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        editableUser = (this[RoomDocumentField.EDITABLE_USER] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
    )
}
