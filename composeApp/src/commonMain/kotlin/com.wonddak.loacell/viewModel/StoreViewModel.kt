package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRoomHelper

class StoreViewModel() : ViewModel() {

    private var roomListenerRegistration: CommonListenerRegistration? = null

    var roomList: List<RoomInfo> by mutableStateOf(emptyList())
        private set

    fun startObserveRoom(
        userId: String,
    ) {
        roomListenerRegistration = CommonRoomHelper.observeAllRoom(userId) {
            roomList = it
        }
    }

    fun stopObserveRoom() {
        roomListenerRegistration?.remove()
        roomList = emptyList()
    }
}