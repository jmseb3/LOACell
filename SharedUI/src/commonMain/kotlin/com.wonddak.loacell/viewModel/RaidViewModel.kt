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
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.RoomType
import com.wonddak.loacell.model.UserInfo
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.network.firebase.model.FBDataItem
import com.wonddak.loacell.network.firebase.model.FBRequest
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.RoomRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class RaidViewModel(
    private val fbApi: FBApi,
    private val roomRepository: RoomRepository,
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
            roomListenerRegistration = roomRepository.observeAll(userId) {
                _roomList.value = it
            }
        }
    }

    fun stopObserveRoom() {
        roomListenerRegistration?.remove()
        roomListenerRegistration = null
        _roomList.value = emptyList()
    }

    fun createRoom(
        title: String,
        description: String,
        password: String,
        owner: String,
        onCreated: () -> Unit,
    ) = roomRepository.create(title, description, password, owner, onCreated)

    fun enterRoom(
        roomId: String,
        password: String,
        alreadyEnteredRoomIds: Set<String>,
        userId: String,
        onEntered: (RoomInfo) -> Unit,
        onError: (String) -> Unit,
    ) {
        if (roomId in alreadyEnteredRoomIds) {
            onError("이미 입장한 방입니다.")
            return
        }
        roomRepository.get(roomId) { roomInfo ->
            when {
                roomInfo == null -> onError("방이 존재 하지 않습니다.")
                roomInfo.enterPassword.isNotEmpty() && password != roomInfo.enterPassword ->
                    onError("방이 존재 하지 않거나 비밀번호가 맞지 않습니다.")
                else -> roomRepository.enter(
                    roomId = roomInfo.uniqueId,
                    userId = userId,
                    successAction = { onEntered(roomInfo) },
                    failAction = { onError("방 입장에 실패 했습니다.") },
                )
            }
        }
    }

    fun exitRoom(
        roomId: String,
        userId: String,
        role: RoomInfo.RoomRole,
        onExited: () -> Unit,
        onFailure: () -> Unit,
    ) = roomRepository.exit(roomId, userId, role, onExited, onFailure)
    //endregion

    //region raidInfo Method
    private var raidListenerRegistration: CommonListenerRegistration? = null
    private var userListenerRegistration: CommonListenerRegistration? = null
    var lastTabIndex = RoomState.Raid.index

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

    fun refreshRole(
        uid: String?,
        roomInfo: RoomInfo
    ) {
        role = roomInfo.getRole(uid)
    }

    fun stopObserveRaidInfo() {
        raidListenerRegistration?.remove()
        userListenerRegistration?.remove()
        raidListenerRegistration = null
        userListenerRegistration = null

        this.lastTabIndex = RoomState.Raid.index
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

    override fun onCleared() {
        stopObserveRoom()
        stopObserveRaidInfo()
        super.onCleared()
    }


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

                val roomId = roomInfo.uniqueId
                val removals = buildList {
                    if (editableUser.isNotEmpty()) {
                        add(async { removeFailedEditableUsers(roomId, editableUser) })
                    }
                    if (enterUser.isNotEmpty()) {
                        add(async { removeFailedEnterUsers(roomId, enterUser) })
                    }
                }
                val completed = withTimeoutOrNull(15_000L) {
                    removals.forEach { it.await() }
                } != null
                if (!completed) {
                    Napier.w { "Timed out while removing unavailable room users: $roomId" }
                }
                initFBData(fbData.data)
            }
        }
    }

    private suspend fun removeFailedEditableUsers(roomId: String, users: List<String>) =
        suspendCancellableCoroutine { continuation ->
            roomRepository.removeEditableUsers(roomId, users) {
                if (continuation.isActive) continuation.resume(Unit)
            }
        }

    private suspend fun removeFailedEnterUsers(roomId: String, users: List<String>) =
        suspendCancellableCoroutine { continuation ->
            roomRepository.removeEnteredUsers(roomId, users) {
                if (continuation.isActive) continuation.resume(Unit)
            }
        }
    //endregion
}
