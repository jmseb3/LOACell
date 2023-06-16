package com.wonddak.loacell.android.viewModel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wonddak.database.AppDataBase
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.RoomRole
import com.wonddak.loacell.RoomState
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.ext.getRole
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.sharedapi.FBDataItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoaCellViewModel(
    private val dataBase: AppDataBase
) : SnackBarController() {

    //로그인 요청후 로그인 프로그레스 출력..
    var loggingIn by mutableStateOf(false)

    //현재 로그인된 유저 정보
    val user get() =  LoaCellApp.user

    //방 클릭시 매핑되는 방 id
    private var _roomId = MutableStateFlow("")
    val roomId get() = _roomId


    //방에 들어갈경우
    fun showRoomInfo(roomId: String) {
        tabState = RoomState.Raid
        _roomId.value = roomId
    }

    //방에서 나갈경우
    fun hideRoomInfo() {
        _roomId.value = ""
        tabState = RoomState.Raid
        tempOfFBData = emptyList()
        hideAllDialog()
        clearFocusItem()
        clearFilter()
    }

    //현재 roomid와 매칭되는 roomInfo
    private var _roomInfo: MutableStateFlow<RoomInfo?> = MutableStateFlow(null)
    val roomInfo get() = _roomInfo

    //owner가 사용자 정보를 볼경우 저장되는 temp값
    var tempOfFBData :List<FBDataItem> by mutableStateOf(emptyList())

    //현재 유저id와 roominfo로 나의 권한 체크
    val myRole = user.combine(roomInfo) { user , info ->
        if (user != null && info != null) {
            info.getRole(user.uid)
        } else {
            RoomRole.NONE
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RoomRole.NONE
    )

    private var _userInfoList: MutableStateFlow<List<UserInfo>> = MutableStateFlow(emptyList())
    val userInfoList get() = _userInfoList

    private var _focusUserName = MutableStateFlow("")
    val focusUserName get() = _focusUserName

    val userInfo = userInfoList.combine(focusUserName) { list, name ->
        list.find { it.name == name }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )
    private var _focusRaidId = MutableStateFlow("")
    val focusRaidId get() = _focusRaidId

    private var _raidInfoList: MutableStateFlow<List<RaidInfo>> = MutableStateFlow(emptyList())
    val raidInfoList get() = _raidInfoList

    val raidInfo = raidInfoList.combine(focusRaidId) {list, id ->
        list.find { it.raidId == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private var raidListInfoJob: Job? = null
    private var userListInfoJob: Job? = null
    private var roomInfoJob: Job? = null

    private var observeRoom: CommonListenerRegistration? = null
    private var observeUser: CommonListenerRegistration? = null
    private var observeRaid: CommonListenerRegistration? = null

    init {
        viewModelScope.launch {
            //room 정보 갱신
            launch {
                roomId.collect { id ->
                    if (id.isNotEmpty()) {
                        CommonRoomHelper.checkExist(
                            id,
                            successAction = {
                                observeRoom = CommonRoomHelper.observe(id,dataBase)
                                roomInfoJob = launch {
                                    dataBase.roomInfoQueriesHelper.getRoomInfoById(id).collect {
                                        _roomInfo.value = it
                                    }
                                }
                                observeUser = CommonUserHelper.observe(id,dataBase)
                                userListInfoJob = launch {
                                    dataBase.userInfoQueriesHelper.getUsersByRoomId(id).collect{
                                        _userInfoList.value = it
                                    }
                                }
                                observeRaid = CommonRaidHelper.observe(id, dataBase)
                                raidListInfoJob = launch {
                                    dataBase.raidInfoQueriesHelper.getAllByRoomId(id).collect {
                                        _raidInfoList.value = it
                                    }
                                }
                            },
                            failAction = {
                                showRoomEnterError = true
                            }
                        )
                    } else {
                        userListInfoJob?.cancel()
                        roomInfoJob?.cancel()
                        raidListInfoJob?.cancel()

                        observeRoom?.remove()
                        observeUser?.remove()
                        observeRaid?.remove()

                        _userInfoList.value = emptyList()
                        _roomInfo.value = null
                        _raidInfoList.value = emptyList()
                    }
                }
            }
        }
    }


    var syncData by mutableStateOf(false)

    fun signOut() {
        hideRoomInfo()
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

    var filterRaidType by mutableStateOf(RaidType.values())
    var filterFinish by mutableIntStateOf(0)
    var filterUser by mutableStateOf(emptyList<String>())

    fun clearFilter() {
        filterRaidType = RaidType.values()
        filterFinish = 0
        filterUser = emptyList()
    }

    //region dialog status
    var showRoomAction by mutableStateOf(false)
    var showRoomAdd by mutableStateOf(false)
    var showRoomEnter by mutableStateOf(false)
    var showRoomEnterError by mutableStateOf(false)
    var showRoomExit by mutableStateOf(false)
    var showRoomEdit by mutableStateOf(false)

    var showUserAdd by mutableStateOf(false)

    var showRaidAdd by mutableStateOf(false)
    var showRaidEdit by mutableStateOf(false)
    var showRaidFilter by mutableStateOf(false)
    var showRaidDelete by mutableStateOf(false)
    var showRaidUserAdd by mutableStateOf(false)
    var showRaidUserDelete by mutableStateOf(false)

    var showCharacterEdit by mutableStateOf(false)
    var showCharacterDelete by mutableStateOf(false)

    var showSetting by mutableStateOf(false)
    var showSettingEditName by mutableStateOf(false)
    fun hideAllDialog() {
        showRoomAction = false
        showRoomAdd = false
        showRoomEnter = false
        showRoomEnterError = false
        showRoomExit = false
        showRoomEdit = false

        showUserAdd = false

        showRaidAdd = false
        showRaidEdit = false
        showRaidFilter = false
        showRaidDelete = false
        showRaidUserAdd = false
        showRaidUserDelete = false

        showCharacterEdit = false
        showCharacterDelete = false

        showSetting = false
        showSettingEditName = false
    }
    // endregion

    var tabState by mutableStateOf(RoomState.Raid)
        private set

    fun setTabStatus(value: RoomState) {
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
                    showRoomAction = true
                }
            }
        } else {
            if (focusUserName.value.isNotEmpty()) {
                Log.i("JWH-B", "22--Focus User")
                showCharacterDelete = true
                return
            }
            if (focusRaidId.value.isNotEmpty()) {
                Log.i("JWH-B", "33--Focus Raid")
                showRaidDelete = true
                return
            }
            when(tabState) {
                RoomState.Raid -> {
                    showRaidAdd = true
                }
                RoomState.User -> {
                    showUserAdd = true
                }
                RoomState.Setting -> {

                }
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