package com.wonddak.loacell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.auth.delete
import com.wonddak.loacell.auth.signOut
import com.wonddak.loacell.ext.SchemeData
import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.ext.getAllInfoByRoomId
import com.wonddak.loacell.ext.getPartyByIndex
import com.wonddak.loacell.model.Dialog
import com.wonddak.loacell.model.Filter
import com.wonddak.loacell.model.Modal
import com.wonddak.loacell.model.ModalConst
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.model.Sheet
import com.wonddak.loacell.model.Synergy
import com.wonddak.loacell.storage.SynergyReferenceHelper
import com.wonddak.loacell.store.CommonListenerRegistration
import com.wonddak.loacell.store.CommonRaidHelper
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.CommonUserHelper
import com.wonddak.loacell.store.FBRaidInfo
import com.wonddak.loacell.store.FBRoomInfo
import com.wonddak.loacell.store.initFBRoomInfo
import com.wonddak.loacell.sharedapi.firebase.model.FBDataItem
import com.wonddak.loacell.sharedapi.lostark.LostArkApi
import com.wonddak.loacell.sharedapi.lostark.model.CharacterInfo
import com.wonddak.loacell.sharedapi.onFail
import com.wonddak.loacell.sharedapi.onFailOnlyMsg
import com.wonddak.loacell.sharedapi.onSuccess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

//동일하게 동작하는 액션을 담아둔 Model
open class CommonViewModel(
    protected val dataBase: AppDataBase,
    protected val config: Config,
    val loginHelper: LoginHelper,
    val synergyReferenceHelper: SynergyReferenceHelper,
) : ViewModel(), DialogAction {

    //현재 로그인된 유저 정보
    protected val userFlow get() = loginHelper.auth.user

    protected val roomListFlow = dataBase.roomInfoQueriesHelper.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = emptyList()
        )
        .toCommonStateFlow()

    //owner가 사용자 정보를 볼경우 저장되는 temp값
    var tempOfFBData: List<FBDataItem> = emptyList()

    private var _roomId = MutableStateFlow("")
    protected val roomIdFlow = _roomId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = ""
        )
        .toCommonStateFlow()

    //방에 들어갈경우
    fun showRoomInfo(roomId: String) {
        CommonRoomHelper.checkExist(
            roomId,
            successAction = {
                _roomId.value = roomId
            },
            failAction = {
                _totalRoomInfo.value =
                    _totalRoomInfo.value.showDialog(Dialog.ROOM_ENTER_ERROR)
            }
        )

    }

    //방에서 나갈경우
    fun hideRoomInfo() {
        _roomId.value = ""
        observeRoom?.remove()
        observeUser?.remove()
        observeRaid?.remove()
        totalJob?.cancel()
        tempOfFBData = emptyList()
        setTabStatus(RoomState.Raid)
        clearFilter()
    }
    //endregion

    private var _totalRoomInfo: MutableStateFlow<TotalRoomInfo> =
        MutableStateFlow(TotalRoomInfo.getInit())
    protected val totalRoomInfoFlow = _totalRoomInfo
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
        get() = _totalRoomInfo.value.getMyRole(userFlow.value?.uid)

    //endregion

    private var observeRoom: CommonListenerRegistration? = null
    private var observeUser: CommonListenerRegistration? = null
    private var observeRaid: CommonListenerRegistration? = null
    private var totalJob: Job? = null

    //region sync 관련
    private var _syncData = MutableStateFlow(false)
    protected val syncDataFlow = _syncData.toCommonStateFlow()

    private fun updateSync(value: Boolean) {
        _syncData.value = value
    }

    private fun syncStart(uid: String, force: Boolean = false) {
        viewModelScope.launch {
            val syncSuccess = {
                updateSync(true)
                CommonRoomHelper.syncRoom(
                    uid,
                    dataBase,
                    failAction = { _ ->
                        showSnackBar("동기화에 실패하였습니다.")
                        updateSync(false)
                    },
                    successAction = {
                        showSnackBar("동기화가 완료되었습니다")
                        updateSync(false)
                    }
                )
            }
            val nowTime = Clock.System.now().toEpochMilliseconds()
            if (force) {
                config.updateHomeRefreshTime(nowTime)
                syncSuccess()
                return@launch
            }
            val syncTime = config.homeRefreshTime.first()
            if (nowTime - syncTime > 60 * 5 * 1000) {
                config.updateHomeRefreshTime(nowTime)
                syncSuccess()
            } else {
                showSnackBar("최근에 동기화를 하여 현재는 할 수 없습니다.")
            }
        }
    }

    fun syncStart(force: Boolean = false) {
        userFlow.value?.let {
            syncStart(it.uid, force)
        }
    }

    fun syncStartForce(uuid: String) {
        syncStart(uuid, true)
    }
    //endregion

    //region filter
    private fun clearFilter() {
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
        _totalRoomInfo.value.bottomAction(roomIdFlow.value, userFlow.value?.isAnonymous)?.let {
            _totalRoomInfo.value = it
        }
    }

    fun topBackAction() {
        if (getSetting()) {
            closeSetting()
        } else {
            if (totalRoomInfoFlow.value.isFocus()) {
                clearFocusItem()
                return
            }
            hideRoomInfo()
        }
    }

    fun signOut() {
        closeSetting()
        hideRoomInfo()
        dataBase.clearAll()
    }

    private var _showLoading = MutableStateFlow(false)
    protected val showLoadingFlow = _showLoading.toCommonStateFlow()
    private var _msg = MutableStateFlow("")
    protected val msgFlow = _msg.toCommonStateFlow()

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
        viewModelScope.launch {
            hideRoomInfo()
            dataBase.roomInfoQueriesHelper.deleteRoomInfo(roomId)
        }
    }

    //login/out
    fun outOrSignOut() {
        if (userFlow.value!!.isAnonymous) {
            loginHelper.delete()
        } else {
            loginHelper.signOut()
        }
        signOut()
    }

    //DialogAction

    val sheetSpaceFlow
        get() = config.sheetSpace

    fun updateSheetSpace(space: Float) {
        viewModelScope.launch {
            config.updateSheetSpace(space)
        }
    }

    val defaultUrlFlow
        get() = config.defaultUrl

    fun updateDefaultUrl(url: String) {
        viewModelScope.launch {
            config.updateDefaultUrl(url)
        }
    }

    //region dialogAction
    fun getDialogAction(): DialogAction = this
    override fun showDialog(modal: Modal) {
        _totalRoomInfo.value = _totalRoomInfo.value.showDialog(modal)
    }

    override fun hideDialog() {
        _totalRoomInfo.value = _totalRoomInfo.value.hideDialog()
    }

    override fun getDisplayName(): String {
        return userFlow.value?.displayName ?: ""
    }

    override fun getRoomListToUniqueId(): List<String> = roomListFlow.value.map { it.uniqueId }
    override fun getTotalRoomInfo(): TotalRoomInfo = _totalRoomInfo.value

    override fun dialogRoomAction(status: Int) {
        when (status) {
            ModalConst.ROOM_ACTION_ENTER -> showDialog(Dialog.ROOM_ENTER)
            ModalConst.ROOM_ACTION_ADD -> showDialog(Sheet.ROOM_ADD)
        }
    }

    override fun dialogRoomAdd(title: String, description: String, password: String) {
        userFlow.value?.uid?.let { owner ->
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
            userFlow.value!!.uid,
            successAction = {
                hideDialog()
            },
            failAction = { error ->
                showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                hideDialog()
            }
        )
        dataBase.initFBRoomInfo(roomInfo, roomId)
    }

    override fun dialogRoomEnterByScheme(roomId: String, roomInfo: FBRoomInfo) {
        dialogRoomEnter(roomId, roomInfo)
        _totalRoomInfo.value = _totalRoomInfo.value.copy(schemeData = null)
    }

    override fun dialogRoomEnterError() {
        viewModelScope.launch {
            dataBase.roomInfoQueriesHelper.deleteRoomInfo(roomIdFlow.value)
            hideRoomInfo()
            hideDialog()
        }
    }

    override fun dialogRoomExit() {
        val roomInfo = totalRoomInfoFlow.value.roomInfo!!
        println("ROOM VM 1")
        CommonRoomHelper.exitRoom(
            roomInfo.uniqueId,
            userFlow.value!!.uid,
            myRole,
            successAction = {
                println("ROOM VM 2")
                deleteRoom(roomInfo.uniqueId)
            },
            failAction = {
                println("ROOM VM 3")
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
                showSnackBar("변경에 실패했습니다(${it.errorMsg}")
            }
        )
    }

    override fun dialogRaidAdd(fbRaidInfo: FBRaidInfo) {
        CommonRaidHelper.add(
            roomIdFlow.value,
            fbRaidInfo,
            { e -> }
        ) {
            hideDialog()
        }
    }

    override fun dialogRaidEdit(fbRaidInfo: FBRaidInfo) {
        val raidInfo = getRaidInfo()
        CommonRaidHelper.update(
            raidInfo.roomId,
            raidInfo.raidId,
            fbRaidInfo.checkLevelParty(totalRoomInfo = getTotalRoomInfo()),
            { e -> }
        ) {
            hideDialog()
        }
    }

    override fun dialogRaidDelete() {
        CommonRaidHelper.delete(
            roomIdFlow.value,
            getRaidInfo().raidId,
            failAction = { error ->
                showSnackBar(error.errorMsg)
            }) {
            clearFocusItem()
            hideDialog()
        }
    }

    override fun dialogUserAdd(character: Character) {
        val focusIndex = _totalRoomInfo.value.focusIndex
        val raidInfo = getRaidInfo()
        val partyIndex = focusIndex / 4
        println("$$$ focusIndex : $focusIndex")
        println("$$$ party Index : $partyIndex")

        val partyTemp = raidInfo.getPartyByIndex(partyIndex)
            .also {
                println("$$$ prev Party $it")
            }
            .toMutableList().also {
                it[focusIndex % 4] = character.name
            }
            .also {
                println("$$$ change Party $it")
            }
        CommonRaidHelper.updatePartList(
            roomIdFlow.value,
            raidInfo.raidId,
            partyIndex + 1,
            partyTemp,
            failAction = { error ->
                showSnackBar("인원 추가에 실패했습니다.\n${error.errorMsg}")
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
        println("$$$ focusIndex : $focusIndex")
        println("$$$ party Index : $partyIndex")

        val partyTemp = raidInfo.getPartyByIndex(partyIndex)
            .also {
                println("$$$ prev Party $it")
            }
            .toMutableList().also {
                it[focusIndex % 4] = ""
            }
            .also {
                println("$$$ change Party $it")
            }

        CommonRaidHelper.updatePartList(
            roomIdFlow.value,
            raidInfo.raidId,
            partyIndex + 1,
            partyTemp,
            failAction = { error ->
                showSnackBar("유저 삭제에 실패했습니다.")
            })
        {
            hideDialog()
        }
    }

    override fun dialogSearchCharacter(
        name: String,
        updateProgress: (Boolean) -> Unit,
        updateList: (List<CharacterInfo>) -> Unit,
        updateError: (String) -> Unit,
    ) {
        viewModelScope.launch {
            updateProgress(true)
            val characterResult = LostArkApi().getCharacterInfo(name)
            characterResult.onSuccess { list ->
                updateList(list)
            }
            characterResult.onFail { code, message ->
                updateError("$message($code)")
            }
            characterResult.onFailOnlyMsg { message ->
                updateError(message)
            }
            updateProgress(false)
        }
    }

    override fun dialogCharacterEdit(name: String) {
        CommonUserHelper.updateRepresentativeCharacter(
            roomIdFlow.value,
            getUserInfo().name,
            name
        )
        hideDialog()
    }

    override fun dialogCharacterDelete() {
        CommonUserHelper.delete(
            roomIdFlow.value,
            getUserInfo().name,
            failAction = { e ->
                showSnackBar(e)
            },
            successAction = {
                clearFocusItem()
                hideDialog()
            }
        )
    }

    override fun dialogFilterUpdate(filter: Filter) {
        _totalRoomInfo.value = _totalRoomInfo.value.updateFilter(filter)
    }

    override fun dialogEditName(name: String) {
        loginHelper.auth.updateDisplayName(name)
        hideDialog()
    }
    //endregion

    fun checkByScheme(
        roomId: String,
    ) {
        if (roomId.isNotEmpty()) {
            hideRoomInfo()
            val nowEnterRoomList = roomListFlow.value.map { it.uniqueId }
            if (nowEnterRoomList.contains(roomId)) {
                showSnackBar("이미 입장한 방입니다.")
            } else {
                CommonRoomHelper.checkExist(
                    roomId,
                    successAction = { roomInfo ->
                        if (roomInfo.enterPassword.isEmpty()) {
                            CommonRoomHelper.enterRoom(
                                roomId,
                                userFlow.value!!.uid,
                                successAction = {
                                    dataBase.initFBRoomInfo(roomInfo, roomId)
                                    showSnackBar("방 정보가 추가되었습니다.")
                                },
                                failAction = { error ->
                                    showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                                }
                            )
                        } else {
                            _totalRoomInfo.value =
                                _totalRoomInfo.value.updateSchemeData(SchemeData(roomId, roomInfo))
                        }
                    },
                    failAction = {
                        showSnackBar("방이 존재 하지 않습니다.")
                    }
                )
            }
        }
    }

    open fun showSnackBar(msg: String) {

    }

    open fun closeSetting() {

    }

    open fun getSetting(): Boolean = true

    init {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                roomIdFlow.collect { id ->
                    if (id.isNotEmpty()) {
                        observeRoom = CommonRoomHelper.observe(id, dataBase)
                        observeUser = CommonUserHelper.observe(id, dataBase)
                        observeRaid = CommonRaidHelper.observe(id, dataBase)
                        totalJob = viewModelScope.launch {
                            dataBase.getAllInfoByRoomId(id).collect {
                                _totalRoomInfo.value = _totalRoomInfo.value.update(it)
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            synergyReferenceHelper.downloadFile { synergyData ->
                Napier.d { "synergyData : $synergyData" }
                Synergy.addData(synergyData)
            }
        }
    }
}