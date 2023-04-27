package com.wonddak.loacell.android.viewModel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LoaCellViewModel : ViewModel() {
    private var _roomId = MutableStateFlow(0L)
    val roomId get() = _roomId


    fun showRoomInfo(roomId:Long) {
        _roomId.value = roomId
    }

    fun hideRoomInfo() {
        _roomId.value = 0
    }

}