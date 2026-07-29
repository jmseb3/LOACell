package com.wonddak.loacell.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaidTypeItem(
    @SerialName("data") val raidData: List<RaidData> = listOf(),
    @SerialName("type") val type: String = "",
)

@Serializable
data class RaidData(
    @SerialName("name") val name: String = "",
    @SerialName("level") val level: List<Level> = listOf(),
)

@Serializable
data class Level(
    @SerialName("difficulty") val difficulty: String = "",
    @SerialName("differentPerGate") val differentPerGate: Boolean = false,
    @SerialName("info") val info: List<Int> = listOf(),
    @SerialName("partySize") val partySize: Int = 0,
) {
    val maxGate: Int
        get() = info.size

    val enterPerson = partySize * 4
}
