package com.wonddak.loacell.model

data class RoomMember(
    val uid: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
) {
    fun displayNameOrFallback(): String = displayName ?: "이름없음($uid)"
}
