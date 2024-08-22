package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import kotlinx.coroutines.launch

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

    val title: String
        get() = roomInfo?.title ?: ""

    var role: RoomInfo.RoomRole by mutableStateOf(RoomInfo.RoomRole.NONE)
        private set

    fun startObserveRaidInfoList(
        uid: String?,
    ) {
        viewModelScope.launch {
            stopObserveRaidInfo()
            roomInfo?.let {
                role = it.getRole(uid)
                raidListenerRegistration =
                    CommonRaidHelper.observe(it.uniqueId) {
                        raidList = it
                    }
            }
        }
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.let {
            it.remove()
            this.raidList = emptyList()
            this.tabState = RoomState.Raid
            this.role = RoomInfo.RoomRole.NONE
        }
    }
}