package com.wonddak.loacell.store

data class FBRoomInfo(
    val title: String = "",
    val description: String = "",
    val owner: String = "",
    val enterPassword: String = "",
    val anonymousUser: List<String> = emptyList(),
    val editableUser: List<String> = emptyList(),
    val enterUser: List<String> = emptyList(),
)

expect object RoomHelper {
    fun syncInfo(
        userId: String,
        successPerDocAction: (id: String, roomInfo: FBRoomInfo) -> Unit,
        failAction: (error: String) -> Unit,
        successAction: () -> Unit
    )
}