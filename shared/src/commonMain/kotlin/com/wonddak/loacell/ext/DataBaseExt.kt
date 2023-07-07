package com.wonddak.loacell.ext

import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class TotalRoomInfo(
    val roomInfo: RoomInfo? = null,
    val raidInfoList: List<RaidInfo> = emptyList(),
    val userInfoList: List<UserInfo> = emptyList()
) {
    fun findUserInfoByName(userName: String): UserInfo? = userInfoList.find { it.name == userName }
    fun findRaidInfoById(raidId: String): RaidInfo? = raidInfoList.find { it.raidId == raidId }
}


fun AppDataBase.getAllInfoByRoomId(id: String): Flow<TotalRoomInfo> {
    val job1 = this.roomInfoQueriesHelper.getRoomInfoById(id)
    val job2 = this.raidInfoQueriesHelper.getAllByRoomId(id)
    val job3 = this.userInfoQueriesHelper.getUsersByRoomId(id)
    return combine(job1, job2, job3) { roomInfo, raidInfoList, userInfoList ->
        TotalRoomInfo(roomInfo, raidInfoList, userInfoList)
    }
}