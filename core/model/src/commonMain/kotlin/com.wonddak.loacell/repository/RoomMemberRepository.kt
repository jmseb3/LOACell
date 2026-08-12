package com.wonddak.loacell.repository

import com.wonddak.loacell.model.RoomMember

data class RoomMemberLookupResult(
    val members: List<RoomMember>,
    val unavailableUserIds: List<String>,
)

interface RoomMemberRepository {
    suspend fun findByIds(userIds: List<String>): RoomMemberLookupResult
}
