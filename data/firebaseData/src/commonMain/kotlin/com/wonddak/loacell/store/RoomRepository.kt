package com.wonddak.loacell.store

import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.repository.Observation
import com.wonddak.loacell.repository.RoomRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/** Firestore 방 문서 접근을 한곳에 모은 데이터 계층 구현체. */
@ContributesBinding(AppScope::class)
@Inject
class FirestoreRoomRepository(
    private val fireStore: CommonFireStore,
) : RoomRepository {
    override fun observeAll(
        userId: String,
        onChanged: (List<RoomInfo>) -> Unit,
    ): Observation {
        val registration = rooms()
        .where(
            CommonFilter.or(
                CommonFilter.equalTo(RoomDocumentField.OWNER, userId),
                CommonFilter.arrayContains(RoomDocumentField.EDITABLE_USER, userId),
                CommonFilter.arrayContains(RoomDocumentField.ENTER_USER, userId),
            )
        )
        .getListenerRegistration(
            successAction = { documents -> onChanged(documents.map { it.toRoomInfo() }) },
            failAction = {},
        )
        return Observation(registration::remove)
    }

    override fun create(
        title: String,
        description: String,
        password: String,
        owner: String,
        onCreated: () -> Unit,
    ) {
        rooms().document().let { room ->
            room.set(
                data = mapOf(
                    RoomDocumentField.TITLE to title,
                    RoomDocumentField.DESCRIPTION to description,
                    RoomDocumentField.OWNER to owner,
                    RoomDocumentField.PASSWORD to password,
                ),
                successAction = onCreated,
                failAction = {},
            )
        }
    }

    override fun get(roomId: String, onResult: (RoomInfo?) -> Unit) {
        room(roomId).get(
            successAction = { snapshot -> onResult(snapshot.takeIf { it.exist }?.toRoomInfo()) },
            failAction = { onResult(null) },
        )
    }

    override fun enter(
        roomId: String,
        userId: String,
        onEntered: () -> Unit,
        onFailure: () -> Unit,
    ) = room(roomId).update(
        field = RoomDocumentField.ENTER_USER,
        value = CommonFieldValue.arrayUnion(userId),
        successAction = onEntered,
        failAction = { onFailure() },
    )

    override fun exit(
        roomId: String,
        userId: String,
        role: RoomInfo.RoomRole,
        onExited: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val field = when (role) {
            RoomInfo.RoomRole.MANAGER -> RoomDocumentField.EDITABLE_USER
            RoomInfo.RoomRole.USER -> RoomDocumentField.ENTER_USER
            RoomInfo.RoomRole.OWNER, RoomInfo.RoomRole.NONE -> return
        }
        room(roomId).update(
            field = field,
            value = CommonFieldValue.arrayRemove(userId),
            successAction = onExited,
            failAction = { onFailure() },
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

    override fun removeEditableUsers(roomId: String, userIds: List<String>, completed: () -> Unit) =
        removeUsers(roomId, userIds, RoomDocumentField.EDITABLE_USER, completed)

    override fun removeEnteredUsers(roomId: String, userIds: List<String>, completed: () -> Unit) =
        removeUsers(roomId, userIds, RoomDocumentField.ENTER_USER, completed)

    override fun update(
        roomId: String,
        title: String,
        description: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) = room(roomId).update(
        data = mapOf(
            RoomDocumentField.TITLE to title,
            RoomDocumentField.DESCRIPTION to description,
            RoomDocumentField.PASSWORD to password,
        ),
        successAction = onSuccess,
        failAction = { onFailure(it.errorMsg) },
    )

    override fun delete(roomId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        room(roomId).delete(
            successAction = onSuccess,
            failAction = { onFailure(it.errorMsg) },
        )
    }

    override fun changeOwner(
        roomId: String,
        previousOwner: String,
        newOwner: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) = fireStore.runBatch(
        write = { batch ->
            val room = room(roomId)
            batch.update(room, RoomDocumentField.ENTER_USER, CommonFieldValue.arrayUnion(previousOwner))
            batch.update(room, RoomDocumentField.OWNER, newOwner)
            batch.update(room, RoomDocumentField.ENTER_USER, CommonFieldValue.arrayRemove(newOwner))
        },
        successAction = onSuccess,
        failAction = { onFailure(it.errorMsg) },
    )

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
