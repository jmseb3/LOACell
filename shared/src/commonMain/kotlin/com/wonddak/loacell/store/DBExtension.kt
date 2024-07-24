package com.wonddak.loacell.store

import com.wonddak.loacell.database.AppDataBase

fun AppDataBase.initFBRoomInfo(roomInfo: FBRoomInfo, roomId: String) {
    this.roomInfoQueriesHelper.addRoomInfo(
        roomInfo.title,
        roomInfo.description,
        roomId,
        roomInfo.owner,
        roomInfo.enterPassword,
        roomInfo.enterUser,
        roomInfo.editableUser,
    )
}