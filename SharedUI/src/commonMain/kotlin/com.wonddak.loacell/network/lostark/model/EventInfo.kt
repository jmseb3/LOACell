package com.wonddak.loacell.network.lostark.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    @SerialName("Title")
    val title: String,
    @SerialName("Thumbnail")
    val thumbnail: String,
    @SerialName("Link")
    val link: String,
    @SerialName("StartDate")
    val startDate: String,
    @SerialName("EndDate")
    val endDate: String,
    @SerialName("RewardDate")
    val rewardDate: String? = null,
)
