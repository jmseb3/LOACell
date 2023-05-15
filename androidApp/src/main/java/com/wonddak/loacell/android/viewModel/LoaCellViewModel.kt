package com.wonddak.loacell.android.viewModel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.android.LoaCellApp
import kotlinx.coroutines.flow.MutableStateFlow

class LoaCellViewModel : ViewModel() {
    private var _roomId = MutableStateFlow("")
    val roomId get() = _roomId

    fun showRoomInfo(roomId:String) {
        tabState = 0
        _roomId.value = roomId
    }

    fun hideRoomInfo() {
        _roomId.value = ""
    }
    val user = LoaCellApp.user

    var showRoomAdd by mutableStateOf(false)
    var showUserAdd by mutableStateOf(false)
    var showRaidAdd by mutableStateOf(false)
    var showSetting by mutableStateOf(false)

    fun clearAllStatus() {
        showRoomAdd = false
        showUserAdd = false
        showRaidAdd = false
        showSetting = false
        hideRoomInfo()
    }
    fun showRoomDialog() {
        if (roomId.value.isEmpty()) {
            showRoomAdd = true
        }
    }
    fun showRaidDialog() {
        if (roomId.value.isNotEmpty()) {
            showRaidAdd = true
        }
    }
    fun hideRaidDialog() {
        showRaidAdd = false
    }
    fun showUserDialog() {
        if (roomId.value.isNotEmpty()) {
            showUserAdd = true
        }
    }

    fun hideUserDialog() {
        showUserAdd = false
    }
    var tabState by  mutableStateOf(0)



}