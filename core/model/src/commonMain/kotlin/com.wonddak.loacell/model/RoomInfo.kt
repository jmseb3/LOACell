package com.wonddak.loacell.model

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
        NONE("-"),
    }

    fun getRole(uid: String?): RoomRole = when {
        uid == null -> RoomRole.NONE
        owner == uid -> RoomRole.OWNER
        uid in editableUser -> RoomRole.MANAGER
        uid in enterUser -> RoomRole.USER
        else -> RoomRole.NONE
    }

    fun getAllUidList(): List<String> = buildList {
        add(owner)
        addAll(editableUser.filter(String::isNotEmpty))
        addAll(enterUser.filter(String::isNotEmpty))
    }
}
