package com.wonddak.loacell.store

import com.wonddak.loacell.model.Character
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.repository.Observation
import com.wonddak.loacell.repository.UserRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.time.Clock

@ContributesBinding(AppScope::class)
@Inject
class FirestoreUserRepository(
    private val fireStore: CommonFireStore,
) : UserRepository {
    override fun observe(roomId: String, onChanged: (List<UserInfo>) -> Unit): Observation {
        val registration = users(roomId).getListenerRegistration(
            successAction = { value -> onChanged(value.documents.map { it.toUserInfo(roomId) }) },
            failAction = {},
        )
        return Observation(registration::remove)
    }

    override fun save(
        roomId: String,
        name: String,
        representativeCharacter: String,
        characters: List<Character>,
        onFailure: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        val data = mapOf(
            UserDocumentField.REPRESENTATIVE_CHARACTER to representativeCharacter,
            UserDocumentField.CHARACTER_LIST to characters.map { character ->
                mapOf(
                    UserDocumentField.NAME to character.name,
                    UserDocumentField.SERVER to character.server,
                    UserDocumentField.CLASS_NAME to character.className,
                    UserDocumentField.LEVEL to character.level,
                )
            },
            UserDocumentField.TIME_STAMP to Clock.System.now().toEpochMilliseconds(),
        )
        val user = users(roomId).document(name)
        user.get(
            successAction = { snapshot ->
                if (snapshot.exist) {
                    user.update(
                        data = data,
                        successAction = onSuccess,
                        failAction = { onFailure(it.errorMsg) },
                    )
                } else {
                    user.set(
                        data = data,
                        successAction = onSuccess,
                        failAction = { onFailure(it.errorMsg) },
                    )
                }
            },
            failAction = { onFailure(it.errorMsg) },
        )
    }

    override fun updateRepresentativeCharacter(userInfo: UserInfo, representativeCharacter: String) {
        users(userInfo.roomId).document(userInfo.name)
            .update(UserDocumentField.REPRESENTATIVE_CHARACTER, representativeCharacter)
    }

    override fun delete(roomId: String, name: String, onFailure: (String) -> Unit, onSuccess: () -> Unit) {
        users(roomId).document(name).delete(
            successAction = onSuccess,
            failAction = { onFailure(it.errorMsg) },
        )
    }

    private fun users(roomId: String): CommonCollection =
        fireStore.collection("rooms").document(roomId).collection("users")
}

private fun CommonDocumentSnapshot.toUserInfo(roomId: String): UserInfo = with(requireNotNull(data)) {
    UserInfo(
        name = this@toUserInfo.id,
        roomId = roomId,
        representativeCharacter = this[UserDocumentField.REPRESENTATIVE_CHARACTER] as String,
        timeStamp = this[UserDocumentField.TIME_STAMP] as Long,
        characterList = this[UserDocumentField.CHARACTER_LIST].asCharacterList()
            .sortedByDescending(Character::getLevel),
    )
}

private fun Any?.asCharacterList(): List<Character> =
    (this as? List<*>)?.mapNotNull { it as? Map<*, *> }?.mapNotNull { character ->
        Character(
            name = character[UserDocumentField.NAME] as? String ?: return@mapNotNull null,
            server = character[UserDocumentField.SERVER] as? String ?: return@mapNotNull null,
            className = character[UserDocumentField.CLASS_NAME] as? String ?: return@mapNotNull null,
            level = character[UserDocumentField.LEVEL] as? String ?: return@mapNotNull null,
        )
    }.orEmpty()
