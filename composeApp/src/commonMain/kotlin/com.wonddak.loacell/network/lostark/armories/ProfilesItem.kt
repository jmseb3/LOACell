package com.wonddak.loacell.network.lostark.armories


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfilesItem(
    @SerialName("CharacterClassName")
    val characterClassName: String,
    @SerialName("CharacterImage")
    val characterImage: String,
    @SerialName("CharacterLevel")
    val characterLevel: Int,
    @SerialName("CharacterName")
    val characterName: String,
    @SerialName("ExpeditionLevel")
    val expeditionLevel: Int,
    @SerialName("GuildMemberGrade")
    val guildMemberGrade: String,
    @SerialName("GuildName")
    val guildName: String,
    @SerialName("ItemAvgLevel")
    val itemAvgLevel: String,
    @SerialName("ItemMaxLevel")
    val itemMaxLevel: String,
    @SerialName("PvpGradeName")
    val pvpGradeName: String,
    @SerialName("ServerName")
    val serverName: String,
    @SerialName("Stats")
    val stats: List<Stat>,
    @SerialName("Tendencies")
    val tendencies: List<Tendency>,
    @SerialName("Title")
    val title: String,
    @SerialName("TotalSkillPoint")
    val totalSkillPoint: Int,
    @SerialName("TownLevel")
    val townLevel: Int,
    @SerialName("TownName")
    val townName: String,
    @SerialName("UsingSkillPoint")
    val usingSkillPoint: Int,
)

@Serializable
data class Stat(
    @SerialName("Tooltip")
    val tooltip: List<String>,
    @SerialName("Type")
    val type: String,
    @SerialName("Value")
    val value: String
)

@Serializable
data class Tendency(
    @SerialName("MaxPoint")
    val maxPoint: Int,
    @SerialName("Point")
    val point: Int,
    @SerialName("Type")
    val type: String
)