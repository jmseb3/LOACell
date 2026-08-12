package com.wonddak.loacell.repository

import com.wonddak.loacell.model.RoomMember
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.network.firebase.model.FBRequest
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultRoomMemberRepository(
    private val fbApi: FBApi,
) : RoomMemberRepository {

    override suspend fun findByIds(userIds: List<String>): RoomMemberLookupResult {
        val response = fbApi.getData(FBRequest(userIds))
        return RoomMemberLookupResult(
            members = response.data.map {
                RoomMember(
                    uid = it.uid,
                    displayName = it.displayName,
                    photoUrl = it.photoURL,
                )
            },
            unavailableUserIds = response.failUidList,
        )
    }
}
