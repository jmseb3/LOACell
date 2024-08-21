package com.wonddak.loacell

import com.wonddak.loacell.model.RoomInfo

object Const {
    const val NAV_LOGIN = "nav_login"

    const val NAV_MAIN = "nav_home"

    const val ARG_ROOM_ID = "roomId"
    const val NAV_ROOM = "nav_room?$ARG_ROOM_ID={$ARG_ROOM_ID}"

    fun RoomInfo.navigationToRoom() = "nav_room?$ARG_ROOM_ID=${this.uniqueId}"
}