package com.wonddak.loacell.store

import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RaidInfoField
import com.wonddak.loacell.model.toRaidInfo
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
        raid(raidInfo.roomId, raidInfo.raidId).update(RaidInfoField.FINISH, !raidInfo.isFinish)
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
