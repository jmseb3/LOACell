package com.wonddak.loacell.ext

import com.wonddak.database.AppDataBase
import com.wonddak.database.ext.getLevel
import com.wonddak.database.ext.getMaxParty
import com.wonddak.database.ext.getMinLevel
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.Character
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * @param[totalRoomInfoSimple] 현재 방정보/ 레이드리스트/ 유저리스트 /캐릭터 리스트를 가지고있는 class
 * @param[focusRaidId] 현재 선택된 레이드 id
 * @param[focusUserName] 현재 선택된 유저 정보
 * @param[tabState] 방에서 선택된 탭 종류
 * @param[dialogState] 현재 노출되는 다이얼로그 종류
 * @param[filter] 필터
 */
data class TotalRoomInfo(
    private val totalRoomInfoSimple: TotalRoomInfoSimple = TotalRoomInfoSimple(),
    val focusRaidId: String = "",
    val focusUserName: String = "",
    val tabState: RoomState = RoomState.Raid,
    val dialogState: DialogStatus = DialogStatus.NONE,
    val filter: Filter = Filter(),
    val focusIndex : Int = -1
) {
    companion object {
        fun getInit(): TotalRoomInfo = TotalRoomInfo()
    }

    val roomInfo: RoomInfo?
        get() = totalRoomInfoSimple.roomInfo
    val raidInfoList: List<RaidInfo>
        get() = totalRoomInfoSimple.raidInfoList
    val userInfoList: List<UserInfo>
        get() = totalRoomInfoSimple.userInfoList
    private val characterMap: Map<UserInfo, List<Character>>
        get() = totalRoomInfoSimple.characterMap

    val userAndCharacterMap: Map<String, List<Character>>
        get() {
            if (raidInfo != null) {
                //현재 레이드 정보에 들어가있는 캐릭터 이름을 가져옴
                val characterNameInParty = raidInfo!!.getPartNameList()

                //현재 레이드 타입에 맞는 것만 필터링 한뒤 파티에 가입된 캐릭터를 모두 추가한다.
                val totalNameList = mutableSetOf<String>()
                raidInfoList
                    .filter { it.type == raidInfo!!.type }
                    .forEach {
                        //각 레이드 정보에있는 캐릭터 이름을 모두 넣는다.
                        totalNameList.addAll(it.party1characterList.filter { it.isNotEmpty() })
                        totalNameList.addAll(it.party2characterList.filter { it.isNotEmpty() })
                    }

                val result: MutableMap<String, List<Character>> = mutableMapOf()
                characterMap.forEach { (user, lc) ->
                    var find = true
                    val lcFilter = lc.filter { it.getLevel() >= raidInfo!!.getMinLevel() }

                    //현재 파티에 추가된 캐릭터가 포함되는 경우 pass한다.
                    for (characterName in characterNameInParty) {
                        val nameList = lcFilter.map { it.name }
                        if (nameList.contains(characterName)) {
                            find = false
                            break
                        }
                    }
                    if (find) {
                        // 현재 파티에 추가되지 않은 경우
                        // 다른곳에 추가된 캐릭터를 제외하고 새로운 리스트를 만든다.(
                        val newList = lcFilter
                            .filter { !totalNameList.contains(it.name) }
                            .sortedByDescending { it.getLevel() }
                        if (newList.isNotEmpty()) {
                            //비어있지 않다면 추가해준다.
                            result[user.name] = newList
                        }
                    }

                }
                return result
            } else {
                return mapOf()
            }
        }

    fun update(
        simple: TotalRoomInfoSimple
    ) = this.copy(
        totalRoomInfoSimple = simple
    )

    fun showRaidId(raidId: String) =
        this.copy(focusRaidId = raidId, focusUserName = "", dialogState = DialogStatus.NONE, focusIndex = -1)

    fun updatePartyFocusIndex(index:Int) = this.copy(focusIndex = index)
    fun showUserName(userName: String) =
        this.copy(focusRaidId = "", focusUserName = userName, dialogState = DialogStatus.NONE)

    val roomId = roomInfo?.uniqueId ?: ""

    //현재 포커싱된 레이드 정보
    val raidInfo: RaidInfo?
        get() = raidInfoList.find { it.raidId == focusRaidId }

    //현재 포커신된 유저 정보
    val userInfo: UserInfo?
        get() = userInfoList.find { it.name == focusUserName }

    //현재 포커싱된 유저의 캐릭터 정보
    val characterList: List<Character>
        get() = characterMap[userInfo] ?: emptyList()

    //나의 역활 정보
    fun getMyRole(uid: String?): RoomRole = roomInfo?.getRole(uid) ?: RoomRole.NONE

    //포커싱 여부
    fun isFocus() = focusUserName.isNotEmpty() || focusRaidId.isNotEmpty()

    fun setTabStatus(state: RoomState) =
        this.copy(tabState = state, dialogState = DialogStatus.NONE)

    fun showDialog(dialogState: DialogStatus) = this.copy(dialogState = dialogState)
    fun hideDialog() = this.copy(dialogState = DialogStatus.NONE)

    fun bottomAction(roomId: String, fbUserIsAnonymous: Boolean?): TotalRoomInfo? {
        if (roomId.isEmpty()) {
            fbUserIsAnonymous?.let { result ->
                if (result) {
                    return showDialog(DialogStatus.ROOM_ENTER)
                } else {
                    return showDialog(DialogStatus.ROOM_ACTION)
                }
            }
        } else {
            if (focusUserName.isNotEmpty()) {
                return showDialog(DialogStatus.CHARACTER_DELETE)
            } else if (focusRaidId.isNotEmpty()) {
                return showDialog(DialogStatus.RAID_DELETE)
            } else {
                when (tabState) {
                    RoomState.Raid ->
                        return showDialog(DialogStatus.RAID_ADD)

                    RoomState.User ->
                        return showDialog(DialogStatus.USER_ADD)

                    RoomState.Setting -> {

                    }
                }
            }
        }
        return null
    }

    val filterList : List<RaidInfo>
        get() {
            val filterByFinish = when (filter.finish) {
                Filter.FINISH.CLEAR -> raidInfoList.filter { it.isFinish }
                Filter.FINISH.NOT_CLEAR -> raidInfoList.filter { !it.isFinish }
                else -> raidInfoList
            }
            val filterByType =
                filterByFinish.filter { filter.raidType.contains(it.type) }

            val filterByUser = if (filter.userList.isEmpty()) {
                filterByType
            } else {
                filterByType.filter {
                    val names = mutableListOf<String>()
                    val filterUser = userInfoList.filter { filter.userList.contains(it.name) }
                    it.getAllPartyList().forEach { character ->
                        if (character.isNotEmpty()) {
                            for (userInfo in filterUser) {
                                val characterList =
                                    (characterMap[userInfo]?.map { it.name } ?: emptyList())
                                if (characterList.contains(character)) {
                                    names.add(userInfo.name)
                                    break
                                }
                            }
                        }
                    }
                    names.sorted() == filter.userList.sorted()
                }
            }
            return filterByUser
        }
    val partyCharacterList: List<Character?>
        get() = raidInfo?.let { info ->
            val maxParty = info.getMaxParty()
            val findList = info.party1characterList.toMutableList()
            if (maxParty == 2) {
                findList.addAll(info.party2characterList)
            }
            val result: MutableList<Character?> = List(findList.size) { null }.toMutableList()
            val findNames = findList.filter { it.isNotEmpty() }.toMutableList()
            for (userInfo in userInfoList) {
                val iterator = findNames.iterator()
                while (iterator.hasNext()) {
                    val name = iterator.next()
                    val find = characterMap[userInfo]?.find { it.name == name }
                    if (find != null) {
                        result[findList.indexOf(name)] = find
                    }
                }
            }
            result
        } ?: emptyList()

    private fun updateFilter(filter: Filter) = this.copy(filter = filter)
    fun updateFilterRaidType(type: RaidType) = updateFilter(this.filter.updateRaidType(type))
    fun updateFilterFinish(finish: Filter.FINISH) = updateFilter(this.filter.updateFinish(finish))
    fun updateFilterUser(user: String) = updateFilter(this.filter.updateUser(user))
    fun updateFilterTimeStep(step: Int) = updateFilter(this.filter.updateTimeStep(step))
    fun updateFilterShowEmptyRow(show: Boolean) =
        updateFilter(this.filter.updateEmptyCalendarRow(show))

    fun clearFilter() = updateFilter(Filter())

}

data class TotalRoomInfoSimple(
    val roomInfo: RoomInfo? = null,
    val raidInfoList: List<RaidInfo> = emptyList(),
    val userInfoList: List<UserInfo> = emptyList(),
    val characterMap: Map<UserInfo, List<Character>> = mapOf(),
)

fun AppDataBase.getAllInfoByRoomId(id: String): Flow<TotalRoomInfoSimple> {
    val job1 = this.roomInfoQueriesHelper.getRoomInfoById(id)
    val job2 = this.raidInfoQueriesHelper.getAllByRoomId(id)
    val job3 = this.userInfoQueriesHelper.getUsersByRoomId(id)
    return combine(job1, job2, job3) { roomInfo, raidInfoList, userInfoList ->
        val characterMap: MutableMap<UserInfo, List<Character>> = mutableMapOf()
        userInfoList.forEach { info ->
            characterMap[info] = this.characterQueriesHelper.getAllList(info)
                .sortedByDescending { it.level.replace(",", "").toFloat() }
        }
        TotalRoomInfoSimple(roomInfo, raidInfoList, userInfoList, characterMap)
    }
}