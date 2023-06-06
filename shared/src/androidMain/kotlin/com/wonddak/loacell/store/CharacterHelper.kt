package com.wonddak.loacell.store

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.model.CharacterInfo

actual object CharacterHelper {
    private fun getCharacterRef(): CollectionReference = Firebase.firestore.collection("characters")

    actual fun addOrUpdate(
        character: CharacterInfo,
        db: AppDataBase
    ) {
        val data = FBCharacter(
            level = character.itemMaxLevel,
            server = character.serverName,
            className = character.characterClassName
        )

        val characterRef = getCharacterRef().document(character.characterName)
        characterRef.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val lastTime = it.data!!["timeStamp"] as Long
                    if (lastTime + 1_800L * 1_000L >= data.timeStamp) {
                        characterRef.update(data.toMap() as Map<String, Any>)
                    }
                } else {
                    characterRef.set(data)
                }
                db.characterInfoQueriesHelper.updateCharacter(
                    character.characterName,
                    data.server,
                    data.className,
                    data.level
                )
            }
    }

    actual fun get(name: String, db: AppDataBase) {
        getCharacterRef().document(name).get().addOnSuccessListener {
            if (it.exists()) {
                val className = it.data!!["className"] as String
                val level = it.data!!["level"] as String
                val server = it.data!!["server"] as String
                db.characterInfoQueriesHelper.updateCharacter(
                    name,
                    server,
                    className,
                    level
                )
            }
        }
    }
}