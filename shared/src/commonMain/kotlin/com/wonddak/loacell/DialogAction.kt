package com.wonddak.loacell

import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.store.FBRoomInfo

interface DialogAction {
    fun showDialog(dialogStatus: DialogStatus)
    fun hideDialog()

    fun getDisplayName() :String
    fun getRoomListToUniqueId(): List<String>
    fun getTotalRoomInfo() : TotalRoomInfo
    fun getRoomInfo(): RoomInfo = getTotalRoomInfo().roomInfo!!
    fun getRoomInfoUniqueId(): String = getRoomInfo().uniqueId
    fun getRaidInfo(): RaidInfo = getTotalRoomInfo().raidInfo!!
    fun getUserInfo(): UserInfo = getTotalRoomInfo().userInfo!!
    fun getCharacterList(): List<Character> = getTotalRoomInfo().characterList
    fun getUserAndCharacterMap() :Map<String,List<Character>> = getTotalRoomInfo().userAndCharacterMap

    //room
    fun dialogRoomAction(status: Int)
    fun dialogRoomAdd(title: String, description: String, password: String)
    fun dialogRoomEnter(roomId: String, roomInfo: FBRoomInfo)
    fun dialogRoomEnterError()
    fun dialogRoomExit()
    fun dialogRoomEdit(title: String, description: String, password: String)

    // raid
    fun dialogRaidDelete()

    // user
    fun dialogUserAdd(character : Character)
    fun dialogUserDelete()

    //character
    fun dialogCharacterEdit(name:String)
    fun dialogCharacterDelete()

    //setting
    fun dialogEditName(name:String)

    //filter
    fun dialogFilterUpdate(filter: Filter)
}