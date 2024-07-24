package com.wonddak.loacell

import com.wonddak.loacell.ext.SchemeData
import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.store.FBRaidInfo
import com.wonddak.loacell.store.FBRoomInfo
import com.wonddak.loacell.sharedapi.lostark.model.CharacterInfo

interface DialogAction {
    fun showDialog(modal: Modal)
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
    fun getSchemeData() :SchemeData? = getTotalRoomInfo().schemeData

    //room
    fun dialogRoomAction(status: Int)
    fun dialogRoomAdd(title: String, description: String, password: String)
    fun dialogRoomEnter(roomId: String, roomInfo: FBRoomInfo)
    fun dialogRoomEnterByScheme(roomId: String, roomInfo: FBRoomInfo)
    fun dialogRoomEnterError()
    fun dialogRoomExit()
    fun dialogRoomEdit(title: String, description: String, password: String)

    // raid

    fun dialogRaidAdd(fbRaidInfo: FBRaidInfo)
    fun dialogRaidEdit(fbRaidInfo: FBRaidInfo)
    fun dialogRaidDelete()

    // user
    fun dialogUserAdd(character : Character)
    fun dialogUserDelete()

    //search 관련
    fun dialogSearchCharacter(
        name:String,
        updateProgress:(Boolean) -> Unit,
        updateList:(List<CharacterInfo>) -> Unit,
        updateError :(String) -> Unit
    )

    //character
    fun dialogCharacterEdit(name:String)
    fun dialogCharacterDelete()

    //setting
    fun dialogEditName(name:String)

    //filter
    fun dialogFilterUpdate(filter: Filter)
}

