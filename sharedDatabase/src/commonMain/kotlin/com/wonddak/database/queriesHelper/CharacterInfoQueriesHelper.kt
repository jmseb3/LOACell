package com.wonddak.database.queriesHelper;

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import com.wonddak.loacell.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class CharacterInfoQueriesHelper(
    private val queries: CharacterQueries
) {

    fun getCharacterValueFlow(userInfo: UserInfo): Flow<List<Character>> {
        return getCharacterValueFlow(userInfo.characterList,userInfo.representativeCharacter)
    }
    fun getCharacterValueFlow(characterNames: List<String>,representativeCharacter:String = ""): Flow<List<Character>> {
        return queries.getCharacterInfos(characterNames).asFlow().mapToList(Dispatchers.Main)
            .transform { list ->
                val sortByLevelList = list.sortedByDescending { it.level.replace(",", "").toFloat() }.toMutableList()

                if (representativeCharacter.isNotEmpty()) {
                    for (i in sortByLevelList.indices) {
                        if (sortByLevelList[i].name == representativeCharacter) {
                            val findItem :Character  = sortByLevelList[i].copy()
                            sortByLevelList.remove(findItem)
                            sortByLevelList.add(0,findItem)
                            break
                        }
                    }
                }

                emit(sortByLevelList)
            }
    }

    fun getCharacterValue(characterNames: List<String>): List<Character> {
        return queries.getCharacterInfos(characterNames).executeAsList()
    }

    fun updateCharacter(
        characterName: String,
        server: String,
        className: String,
        level: String
    ) {
        queries.insertCharacterInfo(
            characterName,
            server,
            className,
            level
        )
    }

    fun deleteCharacter(characterName: String) {
        queries.deleteUserByName(characterName)
    }
}