package com.wonddak.loacell.store

import cocoapods.FirebaseFirestore.FIRCollectionReference
import cocoapods.FirebaseFirestore.FIRFirestore
import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.model.CharacterInfo

actual object CharacterHelper {
    private fun getCharacterRef(): FIRCollectionReference =
        FIRFirestore.firestore().collectionWithPath("characters")

    actual fun addOrUpdate(
        character: CharacterInfo,
        db: AppDataBase
    ) {
        val data = FBCharacter(
            level = character.itemMaxLevel,
            server = character.serverName,
            className = character.characterClassName
        )

        val characterRef = getCharacterRef().documentWithPath(character.characterName)
        characterRef.getDocumentWithCompletion { firDocumentSnapshot, nsError ->
            if (nsError != null) {


            } else {
                if (firDocumentSnapshot?.exists == true) {
                    val lastTime = firDocumentSnapshot.data()!!["timeStamp"] as Long
                    if (lastTime + 1_800L * 1_000L >= data.timeStamp) {
                        characterRef.updateData(data.toMap())
                    }
                } else {
                    characterRef.setData(data.toMap())
                }
                db.characterInfoQueriesHelper.updateCharacter(
                    character.characterName,
                    data.server,
                    data.className,
                    data.level
                )
            }
        }
    }

    actual fun get(name: String, db: AppDataBase) {
        getCharacterRef().documentWithPath(name).getDocumentWithCompletion { firDocumentSnapshot, nsError ->
            if (firDocumentSnapshot?.exists ==true) {
                val className = firDocumentSnapshot.data()!!["className"] as String
                val level = firDocumentSnapshot.data()!!["level"] as String
                val server = firDocumentSnapshot.data()!!["server"] as String
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