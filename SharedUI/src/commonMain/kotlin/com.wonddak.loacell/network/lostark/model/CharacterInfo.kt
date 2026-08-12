package com.wonddak.loacell.network.lostark.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CharacterInfo(
    @SerialName("CharacterClassName")
    val characterClassName: String,
    @SerialName("CharacterLevel")
    val characterLevel: Int,
    @SerialName("CharacterName")
    val characterName: String,
    @SerialName("ItemAvgLevel")
    val itemAvgLevel: String,
    @SerialName("ServerName")
    val serverName: String,
)

@Serializable
data class CharacterProfile(
    @SerialName("CombatPower")
    val combatPower: Long? = null,
)
