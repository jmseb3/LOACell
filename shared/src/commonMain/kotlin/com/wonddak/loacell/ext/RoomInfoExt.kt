package com.wonddak.loacell.ext

import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole

fun RoomInfo.getRole(id: String): RoomRole {
    return if (this.owner == id) {
        RoomRole.OWNER
    } else if (this.editableUser.contains(id)) {
        RoomRole.MANAGER
    } else if (this.enterUser.contains(id)) {
        RoomRole.USER
    } else {
        RoomRole.ANONYMOUS
    }
}

fun RoomInfo.getAllUidList(): List<String> {
    val result = mutableListOf(this.owner)
    result.addAll(this.editableUser.filter { it.isNotEmpty() })
    result.addAll(this.enterUser.filter { it.isNotEmpty() })
    result.addAll(this.anonymousUser.filter { it.isNotEmpty() })
    return result
}