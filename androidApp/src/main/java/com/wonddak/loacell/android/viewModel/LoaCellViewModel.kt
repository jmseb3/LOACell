package com.wonddak.loacell.android.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.LoaCellApp
import kotlinx.coroutines.flow.MutableStateFlow

class LoaCellViewModel : ViewModel() {
    private var _roomId = MutableStateFlow("")
    val roomId get() = _roomId

    fun showRoomInfo(roomId: String) {
        tabState = 0
        _roomId.value = roomId
    }

    fun hideRoomInfo() {
        _roomId.value = ""
        tabState = 0
        clearFocusItem()
    }

    val user = LoaCellApp.user

    var showRoomAdd by mutableStateOf(false)
    var showUserAdd by mutableStateOf(false)
        private set
    var showRaidAdd by mutableStateOf(false)
        private set
    var showSetting by mutableStateOf(false)

    var focusUserInfo: UserInfo? by mutableStateOf(null)
        private set

    var openCharacterEditDialog by mutableStateOf(false)
    var openCharacterDeleteDialog by mutableStateOf(false)
    var focusRaidInfo: RaidInfo? by mutableStateOf(null)
        private set

    fun setNowUserInfo(userInfo: UserInfo) {
        clearFocusItem()
        hideAllDialog()
        focusUserInfo = userInfo
    }
    fun setNowRaidInfo(raidInfo: RaidInfo) {
        clearFocusItem()
        hideAllDialog()
        focusRaidInfo = raidInfo
    }

    fun clearFocusItem() {
        focusRaidInfo = null
        focusUserInfo = null
    }

    fun hideAllDialog() {
        showRoomAdd = false
        showUserAdd = false
        showRaidAdd = false
        showSetting = false
    }

    fun clearAllStatus() {
        hideAllDialog()
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

    var tabState by mutableStateOf(0)
        private set
    fun setTabStatus(value:Int) {
        hideAllDialog()
        tabState = value
    }


    fun bottomAddAction() {
        if (roomId.value.isEmpty()) {
            showRoomDialog()
        } else {
            if (focusUserInfo != null) {
                openCharacterDeleteDialog = true
                return
            }
            if (tabState == 0) {
                showRaidAdd = true
            } else if (tabState == 1) {
                showUserAdd = true
            }
        }
    }

    fun topBackAction() {
        if (showSetting) {
            showSetting = false
        } else {
            hideRoomInfo()
        }
    }


}