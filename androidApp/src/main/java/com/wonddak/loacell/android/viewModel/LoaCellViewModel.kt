package com.wonddak.loacell.android.viewModel


import androidx.lifecycle.ViewModel
import com.wonddak.loacell.android.FireStoreUpdateService
import kotlinx.coroutines.flow.MutableStateFlow

class LoaCellViewModel : ViewModel() {
    private var _roomId = MutableStateFlow("")
    val roomId get() = _roomId


    fun showRoomInfo(roomId:String) {
        _roomId.value = roomId
        FireStoreUpdateService.setRoomId(roomId)
    }

    fun hideRoomInfo() {
        _roomId.value = ""
    }

}