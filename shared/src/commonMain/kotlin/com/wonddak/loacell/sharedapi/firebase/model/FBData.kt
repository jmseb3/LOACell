package com.wonddak.loacell.sharedapi.firebase.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FBRequest(
    @SerialName("id")
    val req: List<String>,
)

@Serializable
data class FBData(
    @SerialName("data")
    var data: List<FBDataItem>,
    @SerialName("fail")
    var failUidList: List<String>
)

@Serializable
data class ProviderData(
    @SerialName("displayName")
    var displayName: String,
    @SerialName("email")
    var email: String,
    @SerialName("photoURL")
    var photoURL: String,
    @SerialName("providerId")
    var providerId: String,
    @SerialName("uid")
    var uid: String
)

@Serializable
data class FBDataItem(
    @SerialName("uid")
    var uid: String,
    @SerialName("displayName")
    var displayName: String? = null,
    @SerialName("photoURL")
    var photoURL: String? = null,
//    @SerialName("providerData")
//    var providerData: List<ProviderData> = emptyList()
) {
    fun getName(): String = displayName ?: "이름없음($uid)"
}