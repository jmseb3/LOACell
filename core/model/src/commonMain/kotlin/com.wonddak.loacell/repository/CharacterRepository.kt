package com.wonddak.loacell.repository

import com.wonddak.loacell.model.Character

sealed interface CharacterLookupResult {
    data class Success(val characters: List<Character>) : CharacterLookupResult
    data class Failure(val message: String) : CharacterLookupResult
}

interface CharacterRepository {
    suspend fun findByName(characterName: String): CharacterLookupResult
}
