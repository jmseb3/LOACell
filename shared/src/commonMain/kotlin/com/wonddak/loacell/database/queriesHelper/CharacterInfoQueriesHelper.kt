package com.wonddak.loacell.database.queriesHelper;

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.wonddak.loacell.Character
import com.wonddak.loacell.CharacterQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CharacterInfoQueriesHelper(
    private val queries: CharacterQueries
) {
    fun getCharacter(characterName: String) : Flow<Character> {
        return queries.getCharacterInfo(characterName).asFlow().mapToOne(Dispatchers.Main)
    }
    fun getCharacterValue(characterName: String) : Character? {
        return try {
            queries.getCharacterInfo(characterName).executeAsOne()
        } catch (e:Exception) {
            null
        }
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