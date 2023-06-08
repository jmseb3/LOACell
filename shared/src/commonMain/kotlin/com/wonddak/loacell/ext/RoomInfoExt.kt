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