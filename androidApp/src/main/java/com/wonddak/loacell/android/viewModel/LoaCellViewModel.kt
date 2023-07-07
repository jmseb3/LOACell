package com.wonddak.loacell.android.viewModel


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wonddak.database.AppDataBase
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.CommonViewModel
import com.wonddak.loacell.Config
import com.wonddak.loacell.DialogStatus
import com.wonddak.loacell.android.LoaCellApp
import com.wonddak.loacell.ext.getRole
import com.wonddak.loacell.model.RoomRole
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.CommonRoomHelper
import com.wonddak.loacell.store.FBRoomInfo
import com.wonddak.loacell.store.initFBRoomInfo
import com.wonddak.sharedapi.firebase.model.FBDataItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoaCellViewModel(
    private val dataBase: AppDataBase,
    private val config: Config
) : SnackBarController() {

    private val common by lazy {
        CommonViewModel(
            viewModelScope,
            dataBase,
            config,
            object : DialogStatus {
                override fun showRoomEnterError() {
                    showRoomEnterError = true
                }

                override fun showSnackBar(msg: String) {
                    showSnackBar(msg, label = "확인")
                }
            }
        )
    }

    //로그인 요청후 로그인 프로그레스 출력..
    var loggingIn by mutableStateOf(false)

    //현재 로그인된 유저 정보
    val user get() = LoaCellApp.user

    //방 클릭시 매핑되는 방 id
    val roomId get() = common.roomId

    //선택된 방의 정보
    val totalRoomInfo get() = common.totalRoomInfo

    //포커싱된 유저 정보
    val focusUserName get() = common.focusUserName
    val userInfo get() =  common.userInfo

    //포커싱된 레이드 정
    val focusRaidId get() = common.focusRaidId
    val raidInfo get() = common.raidInfo

    //방에서 탭 선택
    val tabState get() = common.tabState

    fun setTabStatus(state: RoomState) {
        hideAllDialog()
        common.updateTabState(state)
    }

    //방에 들어갈경우
    fun showRoomInfo(roomId: String) = common.showRoom(roomId)


    //방에서 나갈경우
    fun hideRoomInfo() {
        common.hideRoom()
        tempOfFBData = emptyList()
        hideAllDialog()
        clearFocusItem()
        clearFilter()
    }

    //owner가 사용자 정보를 볼경우 저장되는 temp값
    var tempOfFBData: List<FBDataItem> by mutableStateOf(emptyList())


    //현재 유저id와 roominfo로 나의 권한 체크
    val myRole = user.combine(totalRoomInfo) { user, info ->
        if (user != null && info.roomInfo != null) {
            info.roomInfo!!.getRole(user.uid)
        } else {
            RoomRole.NONE
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = RoomRole.NONE
    )

    private var _showRoomEnterByIntent: MutableStateFlow<String> = MutableStateFlow("")
    val showRoomEnterByIntent get() = _showRoomEnterByIntent
    fun setIntentRoomId(id: String) {
        _showRoomEnterByIntent.value = id
    }

    private var _showRoomEnterPasswordByIntent: MutableStateFlow<Pair<String, FBRoomInfo>?> =
        MutableStateFlow(null)
    val showRoomEnterPasswordByIntent get() = _showRoomEnterPasswordByIntent
    fun clearEnterPasswordByIntent() {
        _showRoomEnterPasswordByIntent.value = null
    }

    init {
        viewModelScope.launch {
            //id 값을 가져온 경우
            launch {
                showRoomEnterByIntent.collect { roomId ->
                    if (roomId.isNotEmpty()) {
                        hideRoomInfo()
                        val nowEnterRoomList =
                            dataBase.roomInfoQueriesHelper.getAllValue().map { it.uniqueId }
                        if (nowEnterRoomList.contains(roomId)) {
                            showSnackBar("이미 입장한 방입니다.")
                        } else {
                            CommonRoomHelper.checkExist(
                                roomId,
                                successAction = { roomInfo ->
                                    if (roomInfo.enterPassword.isEmpty()) {
                                        CommonRoomHelper.enterRoom(
                                            roomId,
                                            user.value!!.uid,
                                            successAction = {
                                                dataBase.initFBRoomInfo(roomInfo, roomId)
                                                _showRoomEnterByIntent.value = ""
                                                showSnackBar("방 정보가 추가되었습니다.")
                                            },
                                            failAction = { error ->
                                                showSnackBar("입장에 실패했습니다.(${error.errorMsg}")
                                            }
                                        )
                                    } else {
                                        _showRoomEnterPasswordByIntent.value =
                                            Pair(roomId, roomInfo)
                                    }
                                },
                                failAction = {
                                    showSnackBar("방이 존재 하지 않습니다.")
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    val syncData get() =  common.syncData
    fun syncStart(force: Boolean = false) = common.syncStart(user.value!!.uid,force)

    fun signOut() {
        hideRoomInfo()
        dataBase.clearAll()
    }

    fun setNowUserInfo(userName: String) {
        hideAllDialog()
        clearFocusItem()
        showLoading = false
        common.updateFocusUserName(userName)
    }

    fun setNowRaidInfo(raidId: String) {
        hideAllDialog()
        clearFocusItem()
        common.updateFocusRaidId(raidId)
    }

    fun clearFocusItem() {
        common.updateFocusRaidId("")
        common.updateFocusUserName("")
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
            when (tabState.value) {
                RoomState.Raid -> { showRaidAdd = true }
                RoomState.User -> { showUserAdd = true }
                RoomState.Setting -> {}
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