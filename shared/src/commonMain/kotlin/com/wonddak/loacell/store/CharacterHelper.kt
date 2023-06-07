package com.wonddak.loacell.store

import com.wonddak.database.AppDataBase
import com.wonddak.sharedapi.model.CharacterInfo
import korlibs.time.DateTime

data class FBCharacter(
    val level: String = "",
    val server: String = "",
    val className: String = "",
    val timeStamp: Long = DateTime.now().milliseconds.toLong(),
) {
    fun toMap(): Map<String, Any> = mapOf(
        "level" to level,
        "server" to server,
        "className" to className,
        "timeStamp" to timeStamp
    )

}
object CommonCharacterHelper {
    fun addOrUpdate(
        character: CharacterInfo,
    ) {
        val data = FBCharacter(
            level = character.itemMaxLevel,
            server = character.serverName,
            className = character.characterClassName
        )
        val characterRef = RefHelper.getCharacterRef().document(character.characterName)
        characterRef.get(
            successAction = {
                if (it.exist) {
                    val lastTime = it.data!!["timeStamp"] as Long
                    if (lastTime + 1_800L * 1_000L >= data.timeStamp) {
                        characterRef.update(data.toMap())
                    }
                } else {
                    characterRef.set(data.toMap())
                }
            },
            failAction = {

            }
        )
    }

    fun get(
        name: String,
        db: AppDataBase
    ) {
        RefHelper.getCharacterRef().document(name).get(
            successAction = {
                if (it.exist) {
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
            },
            failAction = {

            }
        )
    }
}