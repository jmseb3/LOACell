package com.wonddak.loacell.repository

import com.wonddak.loacell.model.RoomInfo

fun interface Observation {
    fun stop()
}

interface RoomRepository {
    fun observeAll(userId: String, onChanged: (List<RoomInfo>) -> Unit): Observation

    fun create(
        title: String,
        description: String,
        password: String,
        owner: String,
        onCreated: () -> Unit,
    )

    fun get(roomId: String, onResult: (RoomInfo?) -> Unit)

    fun enter(
        roomId: String,
        userId: String,
        onEntered: () -> Unit,
        onFailure: () -> Unit,
    )

    fun exit(
        roomId: String,
        userId: String,
        role: RoomInfo.RoomRole,
        onExited: () -> Unit,
        onFailure: () -> Unit,
    )

    fun removeEditableUsers(roomId: String, userIds: List<String>, completed: () -> Unit)

    fun removeEnteredUsers(roomId: String, userIds: List<String>, completed: () -> Unit)

    fun update(
        roomId: String,
        title: String,
        description: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )

    fun delete(roomId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit)

    fun changeOwner(
        roomId: String,
        previousOwner: String,
        newOwner: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
}
