import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.model.UserInfoField
import com.wonddak.loacell.model.toUserInfo
import com.wonddak.loacell.network.lostark.model.CharacterInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.RefHelper
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

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
                    UserInfoField.LEVEL to character.itemMaxLevel,
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