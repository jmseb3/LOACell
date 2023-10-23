package com.wonddak.loacell

import com.wonddak.database.AppDataBase
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.ext.getAllInfoByRoomId
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.loacell.store.FBRoomInfo
import com.wonddak.loacell.store.initFBRoomInfo
import com.wonddak.sharedapi.firebase.model.FBDataItem
import com.wonddak.sharedapi.lostark.LostArkApi
import com.wonddak.sharedapi.onFail
import com.wonddak.sharedapi.onFailOnlyMsg
import com.wonddak.sharedapi.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
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
    }

    //owner가 사용자 정보를 볼경우 저장되는 temp값
    var tempOfFBData: List<FBDataItem> = emptyList()

    fun hideRoom() {
        _roomId.value = ""
        tempOfFBData = emptyList()
        clearFilter()
    }
    //endregion

    //region 방 id 선택시 불러오는 정보
    private var _totalRoomInfo: MutableStateFlow<TotalRoomInfo> =
        MutableStateFlow(TotalRoomInfo.getInit())
    val totalRoomInfo = _totalRoomInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = TotalRoomInfo.getInit()
        )
        .toCommonStateFlow()

    fun updatePartyFocusIndex(index: Int) {
        _totalRoomInfo.value = _totalRoomInfo.value.updatePartyFocusIndex(index)
    }

    private fun updateFocusUserName(name: String) {
        _totalRoomInfo.value = _totalRoomInfo.value.showUserName(name)
    }

    private fun updateFocusRaidId(id: String) {
        _totalRoomInfo.value = _totalRoomInfo.value.showRaidId(id)
    }

    fun setTabStatus(state: RoomState) {
        _totalRoomInfo.value = _totalRoomInfo.value.setTabStatus(state)
    }

    val myRole: RoomRole
        get() = _totalRoomInfo.value.getMyRole(viewModelImpl.getUserUid())

    //endregion

    private var totalRoomJob: Job? = null

    private var observeRoom: CommonListenerRegistration? = null
    private var observeUser: CommonListenerRegistration? = null
    private var observeRaid: CommonListenerRegistration? = null

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
                                        _totalRoomInfo.value = _totalRoomInfo.value.update(it)
                                    }
                                }
                                totalRoomJob?.start()
                            },
                            failAction = {
                                _totalRoomInfo.value =
                                    _totalRoomInfo.value.showDialog(DialogStatus.ROOM_ENTER_ERROR)
                            }
                        )
                    } else {
                        totalRoomJob?.cancel()

                        observeRoom?.remove()
                        observeUser?.remove()
                        observeRaid?.remove()

                        _totalRoomInfo.value = TotalRoomInfo.getInit()
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
    fun updateFilterRaidType(type: RaidType) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilterRaidType(type)
    }

    fun updateFilterFinish(finish: Filter.FINISH) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilterFinish(finish)
    }

    fun updateFilterUser(user: String) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilterUser(user)
    }

    fun updateTimeStep(step: Int) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilterTimeStep(step)
    }

    fun updateShowEmptyRow(show: Boolean) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilterShowEmptyRow(show)
    }

    fun clearFilter() {
        _totalRoomInfo.value = _totalRoomInfo.value.clearFilter()
    }
    //endregion

    //region setAction
    fun setNowUserInfo(userName: String) {
        _showLoading.value = false
        updateFocusUserName(userName)
    }

    fun setNowRaidInfo(raidId: String) {
        updateFocusRaidId(raidId)
    }

    fun clearFocusItem() {
        updateFocusRaidId("")
        updateFocusUserName("")
    }

    fun bottomAddAction() {
        _totalRoomInfo.value.bottomAction(roomId.value, viewModelImpl.fbUserIsAnonymous())?.let {
            _totalRoomInfo.value = it
        }
    }

    fun topBackAction() {
        if (viewModelImpl.getSetting()) {
            viewModelImpl.closeSetting()
        } else {
            if (totalRoomInfo.value.isFocus()) {
                clearFocusItem()
                return
            }
            hideRoom()
        }
    }

    fun signOut() {
        hideRoom()
        dataBase.clearAll()
    }

    private var _showLoading = MutableStateFlow(false)
    val showLoading = _showLoading.toCommonStateFlow()
    private var _msg = MutableStateFlow("")
    val msg = _msg.toCommonStateFlow()

    fun updateCharacter(roomId: String, userInfo: UserInfo) {
        viewModelScope.launch {
            _showLoading.value = true
            _msg.value = "캐릭터 정보를 갱신합니다."
            val characterResult = LostArkApi().getCharacterInfo(userInfo.representativeCharacter)
            characterResult.onSuccess { list ->
                CommonUserHelper.addOrUpdate(
                    roomId = roomId,
                    name = userInfo.name,
                    representativeCharacter = userInfo.representativeCharacter,
                    characterList = list,
                    failAction = { e ->
                        viewModelScope.launch {
                            _msg.value = "서버 데이터 저장에 실패했습니다."
                            delay(1_500L)
                            _showLoading.value = false
                        }

                    }
                ) {
                    viewModelScope.launch {
                        delay(1_000L)
                        _showLoading.value = false
                    }
                }
            }
            characterResult.onFail { code, message ->
                delay(1_500L)
                _msg.value = message
                _showLoading.value
            }
            characterResult.onFailOnlyMsg { message ->
                _msg.value = message
                _showLoading.value
            }
        }
    }
    //endregion

    fun deleteRoom(roomId: String) {
        hideRoom()
        dataBase.roomInfoQueriesHelper.deleteRoomInfo(roomId)
    }

    //DialogAction

    val dialogAction = object : DialogAction {
        override fun showDialog(dialogStatus: DialogStatus) {
            _totalRoomInfo.value = _totalRoomInfo.value.showDialog(dialogStatus)
        }

        override fun hideDialog() {
            _totalRoomInfo.value = _totalRoomInfo.value.hideDialog()
        }

        override fun getRoomListToUniqueId(): List<String> = roomList.value.map { it.uniqueId }
        override fun getTotalRoomInfo(): TotalRoomInfo = _totalRoomInfo.value

        override fun dialogRoomAction(status: Int) {
            when (status) {
                1 -> showDialog(DialogStatus.ROOM_ENTER)
                2 -> showDialog(DialogStatus.ROOM_ADD)
            }
        }

        override fun dialogRoomAdd(title: String, description: String, password: String) {
            viewModelImpl.getUserUid()?.let { owner ->
                CommonRoomHelper.makeInfo(
                    title, description, password, owner
                ) { id ->
                    dataBase.roomInfoQueriesHelper.addRoomInfo(
                        title,
                        description,
                        id,
                        owner,
                        password,
                        emptyList(),
                        emptyList(),
                    )
                }
                hideDialog()
            }
        }

        override fun dialogRoomEnter(roomId: String, roomInfo: FBRoomInfo) {
            CommonRoomHelper.enterRoom(
                roomId,
                viewModelImpl.getUserUid()!!,
                successAction = {
                    hideDialog()
                },
                failAction = { error ->
                    viewModelImpl.showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                    hideDialog()
                }
            )
            dataBase.initFBRoomInfo(roomInfo, roomId)
        }

        override fun dialogRoomEnterError() {
            dataBase.roomInfoQueriesHelper.deleteRoomInfo(roomId.value)
            hideRoom()
            hideDialog()
        }

        override fun dialogRoomExit() {
            val roomInfo = totalRoomInfo.value.roomInfo!!
            CommonRoomHelper.exitRoom(
                roomInfo.uniqueId,
                viewModelImpl.getUserUid()!!,
                myRole,
                successAction = {
                    deleteRoom(roomInfo.uniqueId)
                },
                failAction = {

                }
            )
        }

        override fun dialogRoomEdit(title: String, description: String, password: String) {
            CommonRoomHelper.updateRoom(
                getRoomInfoUniqueId(), title, description, password,
                successAction = {
                    hideDialog()
                },
                failAction = {
                    hideDialog()
                    viewModelImpl.showSnackBar("변경에 실패했습니다(${it.errorMsg}")
                }
            )
        }

        override fun dialogRaidDelete() {
            CommonRaidHelper.delete(
                roomId.value,
                getRaidInfo().raidId,
                failAction = { error ->
                    viewModelImpl.showSnackBar(error.errorMsg)
                }) {
                clearFocusItem()
                hideDialog()
            }
        }

        override fun dialogUserAdd(character: Character) {
            val focusIndex = _totalRoomInfo.value.focusIndex
            val raidInfo = getRaidInfo()
            val partyIndex = focusIndex / 4

            val partyTemp = (
                    if (partyIndex == 0) raidInfo.party1characterList else raidInfo.party2characterList
                    )
                .toMutableList().also {
                    it[focusIndex % 4] = character.name
                }
            CommonRaidHelper.updatePartList(
                roomId.value,
                raidInfo.raidId,
                partyIndex + 1,
                partyTemp,
                failAction = { error ->
                    viewModelImpl.showSnackBar("인원 추가에 실패했습니다.\n${error.errorMsg}")
                })
            {
                updatePartyFocusIndex(focusIndex - 1)
                hideDialog()
            }
        }

        override fun dialogUserDelete() {
            val focusIndex = _totalRoomInfo.value.focusIndex
            val raidInfo = getRaidInfo()
            val partyIndex = focusIndex / 4
            val partyTemp = (
                    if (partyIndex == 0) raidInfo.party1characterList else raidInfo.party2characterList
                    )
                .toMutableList().also {
                    it[focusIndex % 4] = ""
                }
            CommonRaidHelper.updatePartList(
                roomId.value,
                raidInfo.raidId,
                partyIndex + 1,
                partyTemp,
                failAction = { error ->
                    viewModelImpl.showSnackBar("유저 삭제에 실패했습니다.")
                })
            {
                hideDialog()
            }
        }

        override fun dialogCharacterEdit(name: String) {
            CommonUserHelper.updateRepresentativeCharacter(
                roomId.value,
                getUserInfo().name,
                name
            )
            hideDialog()
        }

        override fun dialogCharacterDelete() {
            CommonUserHelper.delete(
                roomId.value,
                getUserInfo().name,
                failAction = { e ->
                    viewModelImpl.showSnackBar(e)
                },
                successAction = {
                    clearFocusItem()
                    hideDialog()
                }
            )
        }
    }
}

interface ViewModelImpl {
    fun showSnackBar(msg: String)

    fun fbUserIsAnonymous(): Boolean?

    fun getUserUid(): String?

    fun closeSetting()
    fun getSetting(): Boolean
}