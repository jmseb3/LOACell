package com.wonddak.loacell.ext

import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.sharedapi.firebase.FBApi
import com.wonddak.loacell.sharedapi.firebase.model.FBDataItem
import com.wonddak.loacell.sharedapi.firebase.model.FBRequest
import kotlinx.coroutines.delay

fun RoomInfo.getRole(id: String?): RoomRole {
    if (id == null) {
        return  RoomRole.NONE
    }
    return if (this.owner == id) {
        RoomRole.OWNER
    } else if (this.editableUser.contains(id)) {
        RoomRole.MANAGER
    } else if (this.enterUser.contains(id)) {
        RoomRole.USER
    } else {
        RoomRole.NONE
    }
}

fun RoomInfo.getAllUidList(): List<String> {
    val result = mutableListOf(this.owner)
    result.addAll(this.editableUser.filter { it.isNotEmpty() })
    result.addAll(this.enterUser.filter { it.isNotEmpty() })
    return result
}

suspend fun RoomInfo.checkNotExistUid(
    success:(data : List<FBDataItem>) -> Unit
) {
    val api = FBApi()
    api.getData(FBRequest(this.getAllUidList())).let { fbData ->
        //실패한 유저 정보 모음을 가져옴
        val failUser = fbData.failUidList
        //실패한 유저가 있는 경우 체크
        val editableUser = this.editableUser.filter { failUser.contains(it) }
        val enterUser = this.enterUser.filter { failUser.contains(it) }

        var result1 = false
        var result2 = false
        val roomId = this.uniqueId
        CommonRoomHelper.exitEditableUserFromRoom(roomId,editableUser){
            result1 = true
        }
        CommonRoomHelper.exitEnterUserFromRoom(roomId,enterUser){
            result2 = true
        }

        while (!result1 || !result2) {
            delay(1_000L)
        }
        success(fbData.data)
    }
}