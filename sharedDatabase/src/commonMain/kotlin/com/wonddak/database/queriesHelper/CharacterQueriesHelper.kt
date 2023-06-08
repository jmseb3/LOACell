package com.wonddak.database.queriesHelper;

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.wonddak.database.ext.getLevel
import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.UserInfoQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

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
        characterName :String
    ) : Character? {
        return queries.selectOneByName(userInfo.name,userInfo.roomId,characterName).executeAsOneOrNull()
    }
}