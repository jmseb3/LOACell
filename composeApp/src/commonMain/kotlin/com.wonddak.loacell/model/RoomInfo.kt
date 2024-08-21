package com.wonddak.loacell.model

import com.wonddak.loacell.store.CommonDocumentSnapshot

object RoomInfoField {
    internal const val TITLE = "title"
    internal const val DESCRIPTION = "description"
    internal const val OWNER = "owner"
    internal const val PASSWORD = "password"
    internal const val EDITABLE_USER = "editableUser"
    internal const val ENTER_USER = "enterUser"
}


data class RoomInfo(
    val uniqueId: String,
    val title: String,
    val description: String,
    val owner: String,
    val enterPassword: String,
    val enterUser: List<String>,
    val editableUser: List<String>,
) {
    enum class RoomRole(val toName: String) {
        OWNER("소유자"),
        MANAGER("관리자"),
        USER("일반 유저"),
        NONE("-")
    }

    fun getRole(uid: String?): RoomRole {
        return uid?.let { uid ->
            if (owner == uid) {
                RoomRole.OWNER
            } else if (editableUser.contains(uid)) {
                RoomRole.MANAGER
            } else if (enterUser.contains(uid)) {
                RoomRole.USER
            } else {
                RoomRole.NONE
            }
        } ?: RoomRole.NONE
    }
}

fun CommonDocumentSnapshot.toRoomInfo(): RoomInfo {
    return with(this.data!!) {
        RoomInfo(
            this@toRoomInfo.id,
            this[RoomInfoField.TITLE] as String,
            this[RoomInfoField.DESCRIPTION] as String,
            this[RoomInfoField.OWNER] as String,
            this[RoomInfoField.PASSWORD] as String,
            this[RoomInfoField.EDITABLE_USER] as List<String>,
            this[RoomInfoField.ENTER_USER] as List<String>,
        )
    }
}