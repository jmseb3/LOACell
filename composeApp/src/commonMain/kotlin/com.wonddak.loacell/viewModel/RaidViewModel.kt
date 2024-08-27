package com.wonddak.loacell.viewModel

import CommonUserHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import kotlinx.coroutines.launch

class RaidViewModel() : ViewModel() {

    private var raidListenerRegistration: CommonListenerRegistration? = null
    var raidList: List<RaidInfo> by mutableStateOf(emptyList())
        private set

    private var userListenerRegistration: CommonListenerRegistration? = null
    var userList: List<UserInfo> by mutableStateOf(emptyList())
        private set

    var roomInfo: RoomInfo? by mutableStateOf(null)

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
                userListenerRegistration =
                    CommonUserHelper.observe(it.uniqueId) {
                        userList = it
                    }
            }
        }
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.remove()
        userListenerRegistration?.remove()

        this.raidList = emptyList()
        this.userList = emptyList()
        this.role = RoomInfo.RoomRole.NONE

    }
}