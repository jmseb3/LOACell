package com.wonddak.loacell.ext

import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.sharedapi.FBApi
import com.wonddak.sharedapi.FBDataItem
import com.wonddak.sharedapi.FBRequest
import kotlinx.coroutines.delay

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

suspend fun RoomInfo.checkNotExistUid(
    success:(data : List<FBDataItem>) -> Unit
) {
    val api = FBApi()
    api.getData(FBRequest(this.getAllUidList())).let { fbData ->
        val failUser = fbData.failUidList
        val anonymousUser = this.anonymousUser.filter { failUser.contains(it) }
        val editableUser = this.editableUser.filter { failUser.contains(it) }
        val enterUser = this.enterUser.filter { failUser.contains(it) }

        var result1 = false
        var result2 = false
        var result3 = false
        CommonRoomHelper.exitUsersFromRoom(
            roomId = this.uniqueId,
            userId = anonymousUser,
            field = "anonymousUser",
            commonAction = {
                result1 = true
            }
        )
        CommonRoomHelper.exitUsersFromRoom(
            roomId = this.uniqueId,
            userId = editableUser,
            field = "editableUser",
            commonAction = {
                result2 = true
            }
        )
        CommonRoomHelper.exitUsersFromRoom(
            roomId = this.uniqueId,
            userId = enterUser,
            field = "enterUser",
            commonAction = {
                result3 = true
            }
        )
        while (!result1 || !result2 || !result3) {
            delay(1_000L)
        }
        success(fbData.data)
    }
}