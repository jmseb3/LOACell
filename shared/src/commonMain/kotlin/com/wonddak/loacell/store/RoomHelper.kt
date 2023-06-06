package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase

data class FBRoomInfo(
    val title: String = "",
    val description: String = "",
    val owner: String = "",
    val enterPassword: String = "",
    val anonymousUser: List<String> = emptyList(),
    val editableUser: List<String> = emptyList(),
    val enterUser: List<String> = emptyList(),
) {
    fun toMap(): Map<Any?, Any> = mapOf(
        "title" to title,
        "description" to description,
        "owner" to owner,
        "enterPassword" to enterPassword,
        "anonymousUser" to anonymousUser,
        "editableUser" to editableUser,
        "enterUser" to enterUser,
    )
}

expect object RoomHelper {
    //로그인시 동기화를 위한 메서드
    fun syncInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error: String) -> Unit,
        successAction: () -> Unit
    )

    // 방 입장요청시 실제 존재하는 방인지 체크
    fun checkExist(
        roomId: String,
        successAction: (password: String) -> Unit,
        failAction: () -> Unit
    )

    // 방 생성시 인포를 넣고 id값 을 가져옴
    fun makeInfo(
        title: String,
        description: String,
        password: String,
        owner: String,
        successAction: (id: String) -> Unit
    )

    // 유저를 방에 추가한다.
    fun updateUser(
        roomId: String,
        userId: String,
        isAnonymous: Boolean,
        successAction: () -> Unit,
        failAction: (e: String?) -> Unit
    )

}