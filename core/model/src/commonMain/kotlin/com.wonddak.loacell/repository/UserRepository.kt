package com.wonddak.loacell.repository

import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.UserInfo

interface UserRepository {
    fun observe(roomId: String, onChanged: (List<UserInfo>) -> Unit): Observation

    fun save(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characters: List<Character>,
        onFailure: (String) -> Unit = {},
        onSuccess: () -> Unit = {},
    )

    fun updateRepresentativeCharacter(userInfo: UserInfo, representativeCharacter: String)

    fun delete(roomId: String, name: String, onFailure: (String) -> Unit, onSuccess: () -> Unit)
}
