package com.wonddak.loacell.viewModel

import CommonUserHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.model.RoomInfo
import com.wonddak.loacell.model.RoomType
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.network.firebase.model.FBDataItem
import com.wonddak.loacell.network.firebase.model.FBRequest
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.CommonRoomHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RaidViewModel(
    private val fbApi: FBApi,
) : ViewModel() {


    //region roomInfo Method
    private var roomListenerRegistration: CommonListenerRegistration? = null

    private var _roomList: MutableStateFlow<List<RoomInfo>> = MutableStateFlow(emptyList())

    val roomList: StateFlow<List<RoomInfo>>
        get() = _roomList

    fun startObserveRoom(
        userId: String,
    ) {
        stopObserveRoom()
        viewModelScope.launch {
            roomListenerRegistration = CommonRoomHelper.observeAllRoom(userId) {
                _roomList.value = it
            }
        }
    }

    fun stopObserveRoom() {
        if (roomListenerRegistration != null) {
            roomListenerRegistration?.remove()
            _roomList.value = emptyList()
        }
    }
    //endregion

    //region raidInfo Method
    private var raidListenerRegistration: CommonListenerRegistration? = null
    private var userListenerRegistration: CommonListenerRegistration? = null

    var raidList: List<RaidInfo> by mutableStateOf(emptyList())
        private set

    var userList: List<UserInfo> by mutableStateOf(emptyList())
        private set

    private fun startObserveRaidInfoList(
        uid: String?,
        roomInfo: RoomInfo,
    ) {
        viewModelScope.launch {
            stopObserveRaidInfo()
            roomInfo.let {
                role = it.getRole(uid)
                raidListenerRegistration =
                    CommonRaidHelper.observe(it.uniqueId) {
                        raidList = it
                    }
                userListenerRegistration =
                    CommonUserHelper.observe(it.uniqueId) {
                        userList = it
                    }
            }
        }
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.remove()
        userListenerRegistration?.remove()

        this.showType = RoomType.Default
        this._filter.value = Filter()
        this.raidList = emptyList()
        this.userList = emptyList()
        this.role = RoomInfo.RoomRole.NONE
    }
    //endregion


    //region Filter
    private var _filter = MutableStateFlow(Filter())

    val filter: StateFlow<Filter>
        get() = _filter

    fun updateFilter(filter: Filter) {
        _filter.value = filter
    }
    //endregion

    var showType by mutableStateOf(RoomType.Default)

    var editItem : RaidInfo? = null

    //선택된 roomId
    private var _roomId: MutableStateFlow<String?> = MutableStateFlow(null)
    val roomId: StateFlow<String?>
        get() = _roomId

    val selectedRoomInfo = roomList.combine(roomId) { list, id ->
        list.find { it.uniqueId == id }
    }

    fun setRoomId(
        roomInfo: RoomInfo,
        uid: String?
    ) {
        _roomId.value = roomInfo.uniqueId
        startObserveRaidInfoList(uid, roomInfo)
    }

    var role: RoomInfo.RoomRole by mutableStateOf(RoomInfo.RoomRole.NONE)
        private set


    //region room setting data
    private var _tempOfFBData = MutableStateFlow(emptyList<FBDataItem>())

    val tempOfFBData: StateFlow<List<FBDataItem>>
        get() = _tempOfFBData

    fun initFBData(data: List<FBDataItem>) {
        fetch = true
        this._tempOfFBData.value = data
    }

    var fetch by mutableStateOf(false)
    fun fetchFBData(roomInfo: RoomInfo) {
        viewModelScope.launch {
            fetch = false
            fbApi.getData(FBRequest(roomInfo.getAllUidList())).let { fbData ->
                //실패한 유저 정보 모음을 가져옴
                val failUser = fbData.failUidList
                //실패한 유저가 있는 경우 체크
                val editableUser = roomInfo.editableUser.filter { failUser.contains(it) }
                val enterUser = roomInfo.enterUser.filter { failUser.contains(it) }

                var result1 = false
                var result2 = false
                val roomId = roomInfo.uniqueId
                CommonRoomHelper.exitEditableUserFromRoom(roomId, editableUser) {
                    result1 = true
                }
                CommonRoomHelper.exitEnterUserFromRoom(roomId, enterUser) {
                    result2 = true
                }

                while (!result1 || !result2) {
                    delay(1_000L)
                }
                initFBData(fbData.data)
            }
        }
    }
    //endregion
}