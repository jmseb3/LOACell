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
)

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