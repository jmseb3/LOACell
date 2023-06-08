package com.wonddak.database.queriesHelper;

import com.wonddak.database.ext.getLevel
import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import com.wonddak.loacell.UserInfo

class CharacterQueriesHelper(
    private val queries: CharacterQueries
) {
    fun getAllList(
        userName: String,
        roomId: String
    ): List<Character> {
        return queries.selectAllByUser(userName = userName, roomId).executeAsList()
    }

    fun getAllList(
        userInfo: UserInfo
    ) = getAllList(userInfo.name, userInfo.roomId)

    fun getAllNameList(
        userInfo: UserInfo
    ) = getAllList(userInfo.name, userInfo.roomId).map { it.name }


    fun getAllListByLevelFilter(
        userInfo: UserInfo,
        minLevel: Int
    ) = getAllList(userInfo).filter { it.getLevel() >= minLevel }

    fun getCharacterInfo(
        userInfo: UserInfo,
        characterName: String
    ): Character? {
        return queries.selectOneByName(userInfo.name, userInfo.roomId, characterName)
            .executeAsOneOrNull()
    }

    fun insertCharacter(
        userInfo: UserInfo,
        name: String,
        server: String,
        className: String,
        level: String
    ) = insertCharacter(userInfo.roomId, userInfo.name, name, server, className, level)

    fun insertCharacter(
        roomId: String,
        userName: String,
        name: String,
        server: String,
        className: String,
        level: String
    ) {
        queries.insertCharacter(roomId, userName, name, server, className, level)
    }
}