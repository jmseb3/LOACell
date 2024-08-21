package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper

class RaidViewModel() : ViewModel() {

    private var raidListenerRegistration: CommonListenerRegistration? = null

    var raidList: List<RaidInfo> by mutableStateOf(emptyList())
        private set

    var tabState: RoomState by mutableStateOf(RoomState.Raid)
        private set

    fun changeTabState(state: RoomState) {
        this.tabState = state
    }

    var roomInfo: RoomInfo? by mutableStateOf(null)
        private set

    val title: String
        get() = roomInfo?.title ?: ""

    var role: RoomInfo.RoomRole by mutableStateOf(RoomInfo.RoomRole.NONE)
        private set

    fun startObserveRaidInfoList(
        roomInfo: RoomInfo?,
        uid: String?,
    ) {
        stopObserveRaidInfo()
        roomInfo ?: return
        this.roomInfo = roomInfo
        this.role = roomInfo.getRole(uid)
        this.raidListenerRegistration = CommonRaidHelper.observe(roomInfo.uniqueId) {
            raidList = it
        }
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.let {
            it.remove()

            this.raidList = emptyList()
            this.tabState = RoomState.Raid
            this.roomInfo = null
            this.role = RoomInfo.RoomRole.NONE
        }
    }
}