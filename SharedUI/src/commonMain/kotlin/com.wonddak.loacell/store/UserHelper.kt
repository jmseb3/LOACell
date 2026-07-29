import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.model.UserInfoField
import com.wonddak.loacell.network.lostark.model.CharacterInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonDocumentSnapshot
import com.wonddak.loacell.store.RefHelper
import io.github.aakira.napier.Napier
import kotlin.time.Clock

private fun CommonDocumentSnapshot.toUserInfo(roomId: String): UserInfo = with(requireNotNull(data)) {
    UserInfo(
        name = this@toUserInfo.id,
        roomId = roomId,
        representativeCharacter = this[UserInfoField.REPRESENTATIVE_CHARACTER] as String,
        timeStamp = this[UserInfoField.TIME_STAMP] as Long,
        characterList = this[UserInfoField.CHARACTER_LIST]
            .asCharacterList()
            .sortedByDescending { it.getLevel() },
    )
}

private fun Any?.asCharacterList(): List<com.wonddak.loacell.model.Character> =
    (this as? List<*>)
        ?.mapNotNull { it as? Map<*, *> }
        ?.mapNotNull { character ->
            val name = character[UserInfoField.NAME] as? String ?: return@mapNotNull null
            val server = character[UserInfoField.SERVER] as? String ?: return@mapNotNull null
            val className = character[UserInfoField.CLASS_NAME] as? String ?: return@mapNotNull null
            val level = character[UserInfoField.LEVEL] as? String ?: return@mapNotNull null
            com.wonddak.loacell.model.Character(name, server, className, level)
        }
        ?: emptyList()

object CommonUserHelper {
    // 방에 유저정보를 추가한다.
    fun addUserInfo(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characterList: List<CharacterInfo>,
        failAction: (e: String) -> Unit = {},
        successAction: () -> Unit = {},
    ) {
        val data = mapOf(
            UserInfoField.REPRESENTATIVE_CHARACTER to representativeCharacter,
            UserInfoField.CHARACTER_LIST to characterList.map { character ->
                mapOf(
                    UserInfoField.NAME to character.characterName,
                    UserInfoField.SERVER to character.serverName,
                    UserInfoField.CLASS_NAME to character.characterClassName,
                    UserInfoField.LEVEL to character.itemAvgLevel,
                )
            },
            UserInfoField.TIME_STAMP to Clock.System.now().toEpochMilliseconds()
        )
        val userRoom = RefHelper.getUserDocRef(roomId, name)

        userRoom.get(
            successAction = {
                if (it.exist) {
                    userRoom.update(
                        data = data,
                        successAction = successAction,
                        failAction = { err ->
                            failAction(err.errorMsg)
                        }
                    )
                } else {
                    runCatching {
                        userRoom.set(
                            data = data,
                            successAction = successAction,
                            failAction = { err ->
                                failAction(err.errorMsg)
                            }
                        )
                    }.onFailure { e ->
                        failAction(e.message ?: "dead")
                    }
                }
            },
            failAction = {
                failAction(it.errorMsg)
            }
        )

    }

    fun updateUserInfo(
        userInfo: UserInfo,
        characterList: List<CharacterInfo>,
    ) {
        addUserInfo(userInfo.roomId, userInfo.name, userInfo.representativeCharacter, characterList)
    }

    // 유저의 대표 캐릭터를 변경한다.
    fun updateRepresentativeCharacter(
        userInfo: UserInfo,
        representativeCharacter: String,
    ) {
        RefHelper.getUserDocRef(userInfo.roomId, userInfo.name)
            .update(UserInfoField.REPRESENTATIVE_CHARACTER, representativeCharacter)
    }

    //유저 정보를 삭제한다.
    fun delete(
        roomId: String,
        name: String,
        failAction: (e: String) -> Unit,
        successAction: () -> Unit,
    ) {
        Napier.d(tag = "CommonUserHelper") { "delete" }
        RefHelper.getUserDocRef(roomId, name)
            .delete(
                successAction = successAction,
                failAction = { failAction(it.errorMsg) }
            )
    }

    fun observe(
        roomId: String,
        successAction: (List<UserInfo>) -> Unit,
    ): CommonListenerRegistration {
        return RefHelper.getUsersRef(roomId).getListenerRegistration(
            successAction = { value ->
                successAction(value.documents.map { it.toUserInfo(roomId) })
            },
            failAction = {

            }
        )
    }
}
