package com.wonddak.sharedapi


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBRequest(
    @SerialName("id")
    val req: List<String>
)

@Serializable
data class FBData(
    @SerialName("data")
    var items: List<FBDataItem>
)

@Serializable
data class FBDataItem(
    @SerialName("uid")
    var uid: String,
    @SerialName("email")
    var email: String? = null,
    @SerialName("displayName")
    var displayName: String? = null,
    @SerialName("photoURL")
    var photoURL: String? = null
)