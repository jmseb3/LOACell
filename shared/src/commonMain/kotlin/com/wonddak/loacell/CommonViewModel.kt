package com.wonddak.loacell

import com.wonddak.database.AppDataBase
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.ext.getAllInfoByRoomId
import com.wonddak.loacell.ext.getRole
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.sharedapi.firebase.model.FBDataItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

//동일하게 동작하는 액션을 담아둔 Model
open class CommonViewModel(
    coroutineScope: CoroutineScope? = null,
    private val dataBase: AppDataBase,
    private val config: Config,
    private val viewModelImpl: ViewModelImpl
) {
    // Ios 의 경우 CoroutineScope(Dispatchers.Main)로 작동
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    val roomList = dataBase.roomInfoQueriesHelper.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = emptyList()
        )
        .toCommonStateFlow()

    //region 방 클릭시 매핑되는 방 id
    private var _roomId = MutableStateFlow("")
    val roomId = _roomId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = ""
        )
        .toCommonStateFlow()

    fun showRoom(roomId: String) {
        _roomId.value = roomId
        updateTabState(RoomState.Raid)
    }
    //owner가 사용자 정보를 볼경우 저장되는 temp값
    var tempOfFBData: List<FBDataItem> = emptyList()

    fun hideRoom() {
        _roomId.value = ""
        updateTabState(RoomState.Raid)
        tempOfFBData = emptyList()
        hideAllDialog()
        clearFocusItem()
        clearFilter()
    }
    //endregion

    //region 방 id 선택시 불러오는 정보
    private var _totalRoomInfo: MutableStateFlow<TotalRoomInfo> = MutableStateFlow(TotalRoomInfo())
    val totalRoomInfo = _totalRoomInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = TotalRoomInfo()
        )
        .toCommonStateFlow()

    //endregion

    //region 유저 이름 선택시
    private var _focusUserName = MutableStateFlow("")
    val focusUserName get() = _focusUserName.toCommonStateFlow()
    fun updateFocusUserName(name: String) {
        _focusUserName.value = name
    }

    val userInfo = totalRoomInfo.combine(focusUserName) { info, name ->
        info.findUserInfoByName(name)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    ).toCommonStateFlow()
    //endregion

    //region 레이드 선택시
    private var _focusRaidId = MutableStateFlow("")
    val focusRaidId get() = _focusRaidId.toCommonStateFlow()
    fun updateFocusRaidId(id: String) {
        _focusRaidId.value = id
    }

    val raidInfo = totalRoomInfo.combine(focusRaidId) { info, id ->
        info.findRaidInfoById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    ).toCommonStateFlow()
    //endregion

    //region tabState
    private var _tabState = MutableStateFlow(RoomState.Raid)
    val tabState = _tabState.toCommonStateFlow()

    fun setTabStatus(state: RoomState) {
        hideAllDialog()
        updateTabState(state)
    }
    private fun updateTabState(state: RoomState) {
        _tabState.value = state
    }
    //endregion

    private var totalRoomJob: Job? = null

    private var observeRoom: CommonListenerRegistration? = null
    private var observeUser: CommonListenerRegistration? = null
    private var observeRaid: CommonListenerRegistration? = null

    //region Role
    val myRole  = totalRoomInfo.transform { info->
        val uid = viewModelImpl.getUserUid()
        val res = if (uid!= null && info.roomInfo != null) {
            info.roomInfo!!.getRole(uid)
        } else {
            RoomRole.NONE
        }
        emit(res)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RoomRole.NONE
    ).toCommonStateFlow()
    //endregion

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                roomId.collect { id ->
                    if (id.isNotEmpty()) {
                        CommonRoomHelper.checkExist(
                            id,
                            successAction = {
                                observeRoom = CommonRoomHelper.observe(id, dataBase)
                                observeUser = CommonUserHelper.observe(id, dataBase)
                                observeRaid = CommonRaidHelper.observe(id, dataBase)
                                totalRoomJob = CoroutineScope(Dispatchers.IO).launch(
                                    start = CoroutineStart.LAZY
                                ) {
                                    dataBase.getAllInfoByRoomId(id).collect {
                                        _totalRoomInfo.value = it
                                    }
                                }
                                totalRoomJob?.start()
                            },
                            failAction = {
                                showDialog(DialogStatus.ROOM_ENTER_ERROR)
                            }
                        )
                    } else {
                        totalRoomJob?.cancel()

                        observeRoom?.remove()
                        observeUser?.remove()
                        observeRaid?.remove()

                        _totalRoomInfo.value = TotalRoomInfo()
                    }
                }
            }
        }
    }

    //region sync 관련
    private var _syncData = MutableStateFlow(false)
    val syncData = _syncData.toCommonStateFlow()
    private fun updateSync(value: Boolean) {
        _syncData.value = value
    }

    fun syncStart(uid: String, force: Boolean = false) {
        viewModelScope.launch {
            val syncSuccess = {
                updateSync(true)
                CommonRoomHelper.syncRoom(
                    uid,
                    dataBase,
                    failAction = { _ ->
                        viewModelImpl.showSnackBar("동기화에 실패하였습니다.")
                        updateSync(false)
                    },
                    successAction = {
                        viewModelImpl.showSnackBar("동기화가 완료되었습니다")
                        updateSync(false)
                    }
                )
            }
            val nowTime = Clock.System.now().toEpochMilliseconds()
            if (force) {
                config.putLong(ConfigKeys.HomeRefreshKey, nowTime)
                syncSuccess()
                return@launch
            }
            val syncTime = config.getLong(ConfigKeys.HomeRefreshKey)
            if (nowTime - syncTime > 60 * 5 * 1000) {
                config.putLong(ConfigKeys.HomeRefreshKey, nowTime)
                syncSuccess()
            } else {
                viewModelImpl.showSnackBar("최근에 동기화를 하여 현재는 할 수 없습니다.")
            }
        }
    }
    //endregion

    //region filter

    private var _filter = MutableStateFlow(Filter())
    val filter get() = _filter.toCommonStateFlow()

    private fun updateFilter(filter: Filter) {
        _filter.value = filter
    }

    fun updateFilterRaidType(type: RaidType) =
        updateFilter(_filter.value.updateRaidType(type))

    fun updateFilterFinish(finish: Filter.FINISH)
    = updateFilter(_filter.value.updateFinish(finish))
    fun updateFilterUser(user: String)
    = updateFilter(_filter.value.updateUser(user))

    fun updateTimeStep(step :Int)
     = updateFilter(_filter.value.updateTimeStep(step))

    fun updateShowEmptyRow(show:Boolean)
            = updateFilter(_filter.value.updateEmptyCalendarRow(show))

    fun clearFilter() {
        _filter.value = Filter()
    }
    //endregion

    //region dialog
    private var _dialogStatus = MutableStateFlow(DialogStatus.NONE)
    val dialogStatus = _dialogStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DialogStatus.NONE
        )
        .toCommonStateFlow()

    fun showDialog(status:DialogStatus) {
        _dialogStatus.value = status
    }

    fun hideAllDialog() {
        _dialogStatus.value = DialogStatus.NONE
    }
    //endregion


    //region setAction
    fun setNowUserInfo(userName: String) {
        hideAllDialog()
        clearFocusItem()
        viewModelImpl.closeLoading()
        updateFocusUserName(userName)
    }

    fun setNowRaidInfo(raidId: String) {
        hideAllDialog()
        clearFocusItem()
        updateFocusRaidId(raidId)
    }

    fun clearFocusItem() {
        updateFocusRaidId("")
        updateFocusUserName("")
    }
    fun bottomAddAction() {
        if (roomId.value.isEmpty()) {
            viewModelImpl.fbUserIsAnonymous()?.let { result ->
                if (result) {
                    showDialog(DialogStatus.ROOM_ENTER)
                } else {
                    showDialog(DialogStatus.ROOM_ACTION)
                }
            }
        } else {
            if (focusUserName.value.isNotEmpty()) {
                showDialog(DialogStatus.CHARACTER_DELETE)
                return
            }
            if (focusRaidId.value.isNotEmpty()) {
                showDialog(DialogStatus.RAID_DELETE)
                return
            }
            when (tabState.value) {
                RoomState.Raid -> {
                    showDialog(DialogStatus.RAID_ADD)
                }

                RoomState.User -> {
                    showDialog(DialogStatus.USER_ADD)
                }

                RoomState.Setting -> {

                }
            }
        }
    }

    fun topBackAction() {
        if (viewModelImpl.getSetting()) {
            viewModelImpl.closeSetting()
        } else {
            hideRoom()
        }
    }

    fun signOut() {
        hideRoom()
        dataBase.clearAll()
    }
    //endregion

}

interface ViewModelImpl {
    fun showSnackBar(msg: String)

    fun fbUserIsAnonymous() : Boolean?

    fun getUserUid():String?

    fun closeSetting()
    fun getSetting() :Boolean

    fun closeLoading()
}