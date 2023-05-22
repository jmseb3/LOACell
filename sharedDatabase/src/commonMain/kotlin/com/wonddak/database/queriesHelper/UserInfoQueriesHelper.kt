package com.wonddak.database.queriesHelper;

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.UserInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class UserInfoQueriesHelper(
    private val queries: UserInfoQueries
) {
    fun addUser(
        userString: String,
        roomId: String,
        representativeCharacter: String,
        characterList: List<String>,
        timeStamp: Long
    ) {
        queries.insertUserInfo(
            userString,
            roomId,
            representativeCharacter,
            characterList,
            timeStamp
        )
    }

    fun updateUserInfo(
        userName: String,
        characterList: List<String>,
        roomId: String,
        representativeCharacter: String,
        timeStamp: Long
    ) {
        queries.updateUserInfo(
            representativeCharacter, characterList, timeStamp, userName, roomId
        )
    }

    fun getUsersByRoomId(roomId: String): Flow<List<UserInfo>> {
        return queries.selectByRoomId(roomId).asFlow()
            .mapToList(Dispatchers.Main)
    }

    fun getUsersByName(roomId: String, userName: String): Flow<UserInfo?> {
        return queries.selectByName(roomId, userName).asFlow()
            .mapToOneOrNull(Dispatchers.Main)
    }

    fun getUsersByRoomIdValue(roomId: String): List<UserInfo> {
        return queries.selectByRoomId(roomId).executeAsList()
    }

    fun deleteUserName(user: String, roomId: String) {
        queries.deleteUserById(user, roomId)
    }
}