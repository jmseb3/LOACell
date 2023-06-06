package com.wonddak.loacell.android.viewModel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.ListenerDoc
import com.wonddak.loacell.store.ObserveHelper
import com.wonddak.loacell.store.RoomHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoaCellViewModel(
    private val dataBase: AppDataBase
) : SnackBarController() {
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

    private var _roomInfo: MutableStateFlow<RoomInfo?> = MutableStateFlow(null)
    val roomInfo get() = _roomInfo

    private var _focusUserName = MutableStateFlow("")
    val focusUserName get() = _focusUserName

    private var _userInfo: MutableStateFlow<UserInfo?> = MutableStateFlow(null)
    val userInfo get() = _userInfo
    private var _focusRaidId = MutableStateFlow("")
    val focusRaidId get() = _focusRaidId

    private var _raidInfoList: MutableStateFlow<List<RaidInfo>> = MutableStateFlow(emptyList())
    val raidInfoList get() = _raidInfoList

    private var _raidInfo: MutableStateFlow<RaidInfo?> = MutableStateFlow(null)
    val raidInfo get() = _raidInfo

    private var userInfoJob: Job? = null
    private var raidListInfoJob: Job? = null
    private var raidInfoJob: Job? = null
    private var roomInfoJob: Job? = null

    private var observeRoom: ListenerDoc? = null
    private var observeUser: ListenerDoc? = null
    private var observeRaid: ListenerDoc? = null

    init {
        viewModelScope.launch {
            //room 정보 갱신
            launch {
                roomId.collect { id ->
                    if (id.isNotEmpty()) {
                        CommonRoomHelper.checkExist(
                            id,
                            successAction = {
                                observeRoom = ObserveHelper.roomInfo(id,dataBase)
                                observeUser = ObserveHelper.users(id,dataBase)
                                observeRaid = ObserveHelper.raidInfo(id, dataBase)
                                roomInfoJob = launch {
                                    dataBase.roomInfoQueriesHelper.getRoomInfoById(id).collect {
                                        _roomInfo.value = it
                                    }
                                }
                                raidListInfoJob = launch {
                                    dataBase.raidInfoQueriesHelper.getALlByRoomId(id).collect {
                                        _raidInfoList.value = it
                                    }
                                }
                            },
                            failAction = {
                                showRoomEnterError = true
                            }
                        )
                    } else {
                        roomInfoJob?.cancel()
                        raidListInfoJob?.cancel()
                        observeRoom?.remove()
                        observeUser?.remove()
                        observeRaid?.remove()
                        _roomInfo.value = null
                        _raidInfoList.value = emptyList()
                    }
                }
            }
            //선택된 유저 정보 갱신
            launch {
                focusUserName.combine(roomId) { name, id ->
                    Pair(name, id)
                }.collect { pair ->
                    val name = pair.first
                    val id = pair.second

                    if (id.isNotEmpty() && name.isNotEmpty()) {
                        userInfoJob = launch {
                            dataBase.userInfoQueriesHelper.getUsersByName(id, name).collect {
                                _userInfo.value = it
                            }
                        }
                    } else {
                        userInfoJob?.cancel()
                        _userInfo.value = null
                    }
                }
            }
            //선택된 레이드 정보 갱신
            launch {
                focusRaidId.combine(roomId) { raidId, roomId ->
                    Pair(raidId, roomId)
                }.collect { pair ->
                    val raidId = pair.first
                    val roomId = pair.second
                    if (raidId.isNotEmpty() && roomId.isNotEmpty()) {
                        raidInfoJob = launch {
                            dataBase.raidInfoQueriesHelper.getRaidInfoById(roomId, raidId).collect {
                                _raidInfo.value = it
                            }
                        }
                    } else {
                        raidInfoJob?.cancel()
                        _raidInfo.value = null
                    }
                }
            }
        }
    }


    var syncData by mutableStateOf(false)

    fun signOut() {
        clearAllStatus()
        dataBase.clearAll()
    }

    fun setNowUserInfo(userName: String) {
        hideAllDialog()
        clearFocusItem()
        showLoading = false
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
    var showRoomDialog by mutableStateOf(false)
    var showRoomAdd by mutableStateOf(false)
    var showRoomEnter by mutableStateOf(false)
    var showRoomEnterError by mutableStateOf(false)
    var showUserAdd by mutableStateOf(false)
    var showRaidAdd by mutableStateOf(false)
    var showSetting by mutableStateOf(false)
    fun hideAllDialog() {
        showRoomDialog = false
        showRoomAdd = false
        showRoomEnter = false
        showRoomEnterError = false
        showUserAdd = false
        showRaidAdd = false
        showSetting = false
    }

    var openCharacterEditDialog by mutableStateOf(false)
    var openCharacterDeleteDialog by mutableStateOf(false)
    var openRaidDeleteDialog by mutableStateOf(false)
    var openRaidUserAddDialog by mutableStateOf(false)
    var openRaidUserDeleteDialog by mutableStateOf(false)
    // endregion

    var tabState by mutableStateOf(0)
        private set

    fun setTabStatus(value: Int) {
        hideAllDialog()
        tabState = value
    }

    var showLoading by mutableStateOf(false)

    fun bottomAddAction() {
        if (roomId.value.isEmpty()) {
            LoaCellApp.user.value?.let { userInfo ->
                if (userInfo.isAnonymous) {
                    showRoomEnter = true
                } else {
                    showRoomDialog = true
                }
            }
        } else {
            if (focusUserName.value.isNotEmpty()) {
                Log.i("JWH-B", "22--Focus User")
                openCharacterDeleteDialog = true
                return
            }
            if (focusRaidId.value.isNotEmpty()) {
                Log.i("JWH-B", "33--Focus Raid")
                openRaidDeleteDialog = true
                return
            }
            if (tabState == 0) {
                Log.i("JWH-B", "33- ShowRaid")
                showRaidAdd = true
            } else if (tabState == 1) {
                Log.i("JWH-B", "44-- ShowUser")
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