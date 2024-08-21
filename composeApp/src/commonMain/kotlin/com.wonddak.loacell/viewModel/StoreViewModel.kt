package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRoomHelper
import io.github.aakira.napier.Napier

class StoreViewModel() : ViewModel() {

    private var roomListenerRegistration: CommonListenerRegistration? = null

    var roomList: List<RoomInfo> by mutableStateOf(emptyList())
        private set

    fun startObserveRoom(
        userId: String,
    ) {
        stopObserveRoom()
        roomListenerRegistration = CommonRoomHelper.observeAllRoom(userId) {
            this@StoreViewModel.roomList = it
            roomList.forEach {
                Napier.d(tag = "aa!!2") { ">>> $it" }
            }
        }
    }

    fun findRoomInfo(roomId: String?): RoomInfo? {
        Napier.d(tag = "aa!!") { "roomId >>> $roomId" }
        Napier.d(tag = "aa!!") { "roomList ==  >>> $roomList" }
        var find: RoomInfo? = null
        for (roomInfo in roomList) {
            Napier.d(tag = "aa!!") { ">>> $roomInfo" }
            if (roomInfo.uniqueId == roomId) {
                find = roomInfo
                break
            }
        }
        return find
    }

    fun stopObserveRoom() {
        if (roomListenerRegistration != null) {
            roomListenerRegistration?.remove()
            roomList = emptyList()
        }
    }
}