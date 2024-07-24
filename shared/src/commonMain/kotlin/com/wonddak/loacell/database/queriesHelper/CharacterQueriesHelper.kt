package com.wonddak.loacell.database.queriesHelper;

import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import com.wonddak.loacell.UserInfo

class CharacterQueriesHelper(
    private val queries: CharacterQueries,
) {
    fun getAllList(
        userName: String,
        roomId: String,
    ): List<Character> {
        return queries.selectAllByUser(userName = userName, roomId).executeAsList()
    }

    fun getAllList(
        userInfo: UserInfo,
    ) = getAllList(userInfo.name, userInfo.roomId)

    fun insertCharacter(
        roomId: String,
        userName: String,
        name: String,
        server: String,
        className: String,
        level: String,
    ) {
        queries.insertCharacter(roomId, userName, name, server, className, level)
    }
}