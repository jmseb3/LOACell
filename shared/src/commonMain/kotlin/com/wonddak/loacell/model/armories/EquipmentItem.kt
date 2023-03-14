package com.wonddak.loacell.model.armories


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EquipmentItem(
    @SerialName("Grade")
    val grade: String,
    @SerialName("Icon")
    val icon: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Tooltip")
    val tooltip: String,
    @SerialName("Type")
    val type: String
)
