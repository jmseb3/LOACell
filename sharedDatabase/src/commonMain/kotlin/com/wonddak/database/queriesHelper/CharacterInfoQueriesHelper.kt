package com.wonddak.database.queriesHelper;

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CharacterInfoQueriesHelper(
    private val queries: CharacterQueries
) {
    fun getCharacterValueFlow(characterNames: List<String>) : Flow<List<Character>> {
        return queries.getCharacterInfos(characterNames).asFlow().mapToList(Dispatchers.Main)
    }

    fun getCharacterValue(characterNames: List<String>) : List<Character> {
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