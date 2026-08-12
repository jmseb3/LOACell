package com.wonddak.loacell.store

import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.repository.RaidRepository
import com.wonddak.loacell.repository.Observation
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@ContributesBinding(AppScope::class)
@Inject
class FirestoreRaidRepository(
    private val fireStore: CommonFireStore,
) : RaidRepository {
    override fun observe(roomId: String, onChanged: (List<RaidInfo>) -> Unit): Observation {
        val registration = raids(roomId).getListenerRegistration(
            successAction = { value -> onChanged(value.documents.map { it.toRaidInfo(roomId) }) },
            failAction = {},
        )
        return Observation(registration::remove)
    }

    override fun add(raidInfo: RaidInfo, onFailure: () -> Unit, onSuccess: () -> Unit) {
        raids(raidInfo.roomId).document().set(
            data = raidInfo.toMap(),
            successAction = onSuccess,
            failAction = { onFailure() },
        )
    }

    override fun update(raidInfo: RaidInfo, onFailure: () -> Unit, onSuccess: () -> Unit) {
        raid(raidInfo.roomId, raidInfo.raidId).update(
            data = raidInfo.toMap(),
            successAction = onSuccess,
            failAction = { onFailure() },
        )
    }

    override fun delete(roomId: String, raidId: String, onFailure: () -> Unit, onSuccess: () -> Unit) {
        raid(roomId, raidId).delete(
            successAction = onSuccess,
            failAction = { onFailure() },
        )
    }

    override fun toggleFinish(raidInfo: RaidInfo) {
        raid(raidInfo.roomId, raidInfo.raidId).update(RaidDocumentField.FINISH, !raidInfo.isFinish)
    }

    override fun updateParty(
        roomId: String,
        raidId: String,
        partyNumber: Int,
        party: List<String>,
        completed: () -> Unit,
    ) {
        raid(roomId, raidId).update(
            field = "party$partyNumber",
            value = party,
            successAction = completed,
            failAction = { completed() },
        )
    }

    private fun raids(roomId: String): CommonCollection =
        fireStore.collection("rooms").document(roomId).collection("raidInfo")

    private fun raid(roomId: String, raidId: String): CommonDocument = raids(roomId).document(raidId)
}

private fun RaidInfo.toMap(): Map<String, Any> = mapOf(
    RaidDocumentField.TITLE to title,
    RaidDocumentField.TYPE to type,
    RaidDocumentField.DIFFICULTY to difficulty,
    RaidDocumentField.START_GATE_NUMBER to startGateNumber,
    RaidDocumentField.END_GATE_NUMBER to endGateNumber,
    RaidDocumentField.FINISH to isFinish,
    RaidDocumentField.PARTY_1 to party1characterList,
    RaidDocumentField.PARTY_2 to party2characterList,
    RaidDocumentField.PARTY_3 to party3characterList,
    RaidDocumentField.PARTY_4 to party4characterList,
    RaidDocumentField.DAY to dayIndex,
    RaidDocumentField.HOUR to hour,
    RaidDocumentField.MINUTE to minute,
)

private fun CommonDocumentSnapshot.toRaidInfo(roomId: String): RaidInfo = with(requireNotNull(data)) {
    RaidInfo(
        raidId = this@toRaidInfo.id,
        roomId = roomId,
        title = this[RaidDocumentField.TITLE] as String,
        type = this[RaidDocumentField.TYPE] as String,
        difficulty = this[RaidDocumentField.DIFFICULTY] as String,
        startGateNumber = (this[RaidDocumentField.START_GATE_NUMBER] as Long).toInt(),
        endGateNumber = (this[RaidDocumentField.END_GATE_NUMBER] as Long).toInt(),
        isFinish = this[RaidDocumentField.FINISH] as Boolean,
        party1characterList = this[RaidDocumentField.PARTY_1].asStringList(),
        party2characterList = this[RaidDocumentField.PARTY_2].asStringList(),
        party3characterList = this[RaidDocumentField.PARTY_3].asStringList(List(4) { "" }),
        party4characterList = this[RaidDocumentField.PARTY_4].asStringList(List(4) { "" }),
        dayIndex = (this[RaidDocumentField.DAY] as? Long) ?: -1,
        hour = ((this[RaidDocumentField.HOUR] as? Long) ?: 0).toInt(),
        minute = ((this[RaidDocumentField.MINUTE] as? Long) ?: 0).toInt(),
    )
}

private fun Any?.asStringList(default: List<String> = emptyList()): List<String> =
    (this as? List<*>)?.filterIsInstance<String>() ?: default
