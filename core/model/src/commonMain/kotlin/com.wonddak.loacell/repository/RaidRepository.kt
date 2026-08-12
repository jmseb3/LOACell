package com.wonddak.loacell.repository

import com.wonddak.loacell.model.RaidInfo

interface RaidRepository {
    fun observe(roomId: String, onChanged: (List<RaidInfo>) -> Unit): Observation

    fun add(raidInfo: RaidInfo, onFailure: () -> Unit, onSuccess: () -> Unit)

    fun update(raidInfo: RaidInfo, onFailure: () -> Unit, onSuccess: () -> Unit)

    fun delete(roomId: String, raidId: String, onFailure: () -> Unit, onSuccess: () -> Unit)

    fun toggleFinish(raidInfo: RaidInfo)

    fun updateParty(
        roomId: String,
        raidId: String,
        partyNumber: Int,
        party: List<String>,
        completed: () -> Unit,
    )
}
