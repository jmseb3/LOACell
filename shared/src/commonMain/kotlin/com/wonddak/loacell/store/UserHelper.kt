package com.wonddak.loacell.store

import com.wonddak.sharedapi.model.CharacterInfo
import korlibs.time.DateTime

data class FBUSerInfo(
    val representativeCharacter: String = "",
    val characterList: List<String> = emptyList(),
    val timeStamp: Long = DateTime.now().milliseconds.toLong(),
) {
    fun toMap(): HashMap<Any?, Any> {
        val data = HashMap<Any?, Any>()
        data["representativeCharacter"] = representativeCharacter
        data["characterList"] = characterList
        data["timeStamp"] = timeStamp
        return data
    }
}

expect object UserHelper {

    // 방에 유저정보를 추가한다.
    fun add(
        roomId:String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    )

    // 유저의 대표 캐릭터를 변경한다.
    fun updateRepresentativeCharacter(
        roomId: String,
        name: String,
        representativeCharacter: String
    )

    //유저 정보를 삭제한다.
    fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit
    )
}