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

object CommonRoomHelper {
    private fun getRoomsRef(): CommonCollection = getFireStore().collection("rooms")
    private fun getRoomRef(id: String): CommonDocument = getRoomsRef().document(id)

    fun syncInfo(
        userId: String,
        db: AppDataBase,
        failAction: (error: Error) -> Unit,
        successAction: () -> Unit
    ) {
        getRoomsRef()
            .where(
                CommonFilter.or(
                    CommonFilter.equalTo("owner", userId),
                    CommonFilter.arrayContains("anonymousUser", userId),
                    CommonFilter.arrayContains("editableUser", userId),
                    CommonFilter.arrayContains("enterUser", userId),
                )
            ).get(
                successAction = { querySnapshot ->
                    querySnapshot.documents.forEach { document ->
                        val id = document.id
                        val data = document.data!!
                        val title = data["title"] as String
                        val description = data["description"] as String
                        val owner = data["owner"] as String
                        db.roomInfoQueriesHelper.addRoomInfo(
                            title = title,
                            description = description,
                            uniqueId = id,
                            owner = owner
                        )
                    }
                    successAction()
                },
                failAction = failAction
            )

    }

    fun checkExist(
        roomId: String,
        successAction: (password: String) -> Unit,
        failAction: () -> Unit
    ) {
        getRoomRef(roomId)
            .get(
                successAction = {
                    if (it.exist) {
                        successAction(it.data!!["enterPassword"] as String)
                    } else {
                        failAction()
                    }

                },
                failAction = {failAction()}
            )
    }
}