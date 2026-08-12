package com.wonddak.loacell.repository

import com.wonddak.loacell.model.Character
import com.wonddak.loacell.network.LostArkResult
import com.wonddak.loacell.network.lostark.LostArkApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class DefaultCharacterRepository(
    private val lostArkApi: LostArkApi,
) : CharacterRepository {

    override suspend fun findByName(characterName: String): CharacterLookupResult =
        when (val result = lostArkApi.getCharacterInfo(characterName)) {
            is LostArkResult.Success -> CharacterLookupResult.Success(
                result.data.map {
                    Character(
                        name = it.characterName,
                        server = it.serverName,
                        className = it.characterClassName,
                        level = it.itemAvgLevel,
                    )
                },
            )
            is LostArkResult.Fail -> CharacterLookupResult.Failure(
                "${result.message}(${result.code})",
            )
            is LostArkResult.FailOnlyMsg -> CharacterLookupResult.Failure(result.message)
        }
}
