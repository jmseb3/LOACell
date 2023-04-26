package com.wonddak.loacell.api.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


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
    @SerialName("ItemMaxLevel")
    val itemMaxLevel: String,
    @SerialName("ServerName")
    val serverName: String
)