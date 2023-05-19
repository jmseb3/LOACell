package com.wonddak.sharedapi.model

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
    @SerialName("ItemMaxLevel")
    val itemMaxLevel: String,
    @SerialName("ServerName")
    val serverName: String
)