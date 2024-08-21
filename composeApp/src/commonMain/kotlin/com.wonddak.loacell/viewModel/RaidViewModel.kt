package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper

class RaidViewModel() : ViewModel() {

    private var raidListenerRegistration: CommonListenerRegistration? = null

    var raidList: List<RaidInfo> by mutableStateOf(emptyList())
        private set

    var tabState: RoomState by mutableStateOf(RoomState.Raid)

    var roomId: String? = null
        private set

    fun startObserveRaidInfoList(
        roomId: String?,
    ) {
        stopObserveRaidInfo()
        roomId ?: return
        this.roomId = roomId
        this.raidListenerRegistration = CommonRaidHelper.observe(roomId) {
            raidList = it
        }
    }

    fun stopObserveRaidInfo() {
        this.raidListenerRegistration?.remove()
        this.raidList = emptyList()
        this.roomId = null
    }
}