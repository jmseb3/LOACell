package com.wonddak.loacell.android.viewModel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.util.FireStoreHelper
import com.wonddak.loacell.database.AppDataBase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaCellViewModel(
    dataBase: AppDataBase
) : ViewModel() {
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
    private var _roomInfo :MutableStateFlow<RoomInfo?> = MutableStateFlow(null)
    val roomInfo get() = _roomInfo

    private var _focusUserName = MutableStateFlow("")
    val focusUserName get() = _focusUserName

    private var _userInfo :MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userInfo get() = _userInfo
    private var _focusRaidId = MutableStateFlow("")
    val focusRaidId get() = _focusRaidId

    private var userInfoJob : Job? = null
    init {
        viewModelScope.launch {
            launch {
                roomId.collect {id ->
                    if (id.isNotEmpty()) {
                        _roomInfo.value = dataBase.roomInfoQueriesHelper.getRoomInfoById(id)
                        FireStoreHelper.observeUsers(id, dataBase)
                        FireStoreHelper.observeRaid(id, dataBase)

                        launch {
                            focusUserName.collect {name ->
                                Log.i("JWH-ffName",name)
                                if (name.isNotEmpty()) {
                                    userInfoJob = launch {
                                        dataBase.getUsersByName(id,name).collect {
                                            _userInfo.value = it
                                            Log.i("JWH-ttt",it.toString())
                                        }
                                    }
                                } else {
                                    userInfoJob?.cancel()
                                    _userInfo.value = null
                                }
                            }
                        }

                    } else {
                        _roomInfo.value = null
                    }
                }
            }
        }
    }


    fun setNowUserInfo(userName: String) {
        hideAllDialog()
        clearFocusItem()
        _focusUserName.value = userName
    }

    fun setNowRaidInfo(raidId: String) {
        hideAllDialog()
        clearFocusItem()
        _focusRaidId.value = raidId
    }

    fun clearFocusItem() {
        _focusRaidId.value = ""
        _focusUserName.value = ""
    }


    fun clearAllStatus() {
        hideAllDialog()
        hideRoomInfo()
    }

    //region dialog status
    var showRoomAdd by mutableStateOf(false)
    var showUserAdd by mutableStateOf(false)
        private set
    var showRaidAdd by mutableStateOf(false)
        private set
    var showSetting by mutableStateOf(false)
    fun hideAllDialog() {
        showRoomAdd = false
        showUserAdd = false
        showRaidAdd = false
        showSetting = false
    }

    var openCharacterEditDialog by mutableStateOf(false)
    var openCharacterDeleteDialog by mutableStateOf(false)
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
    // endregion

    var tabState by mutableStateOf(0)
        private set
    fun setTabStatus(value:Int) {
        hideAllDialog()
        tabState = value
    }

    var showRoomInfo by mutableStateOf(false)


    fun bottomAddAction() {
        if (roomId.value.isEmpty()) {
            showRoomDialog()
        } else {
            if (focusUserName.value.isNotEmpty()) {
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