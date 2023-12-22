package com.wonddak.loacell.android.viewModel


import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.database.AppDataBase
import com.wonddak.loacell.CommonViewModel
import com.wonddak.loacell.Config
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.ViewModelImpl
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.model.DialogStatus
import com.wonddak.loacell.model.RoomState
import com.wonddak.loacell.store.FBRoomInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoaCellViewModel(
    private val dataBase: AppDataBase,
    private val config: Config,
    val loginHelper: LoginHelper
) : ViewModel(), ViewModelImpl {

    private val common by lazy {
        CommonViewModel(
            viewModelScope,
            dataBase,
            config,
            loginHelper,
            this@LoaCellViewModel
        )
    }
    private val snackBarController = SnackBarController()

    val snackBarMessage
        get() = snackBarController.snackBarMessage

    fun showSnackBar(
        message: String,
        label: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: () -> Unit = { resetSnackBar() },
    ) = snackBarController.showSnackBar(message, label, duration, action)

    override fun showSnackBar(msg: String) {
        showSnackBar(msg, label = "확인")
    }

    fun resetSnackBar() = snackBarController.resetSnackBar()

    //현재 로그인된 유저 정보
    val user get() = common.user

    // 전체 room 정보
    val roomList get() = common.roomList

    //방 클릭시 매핑되는 방 id
    val roomId get() = common.roomId

    //선택된 방의 정보
    val totalRoomInfo get() = common.totalRoomInfo

    fun setTabStatus(state: RoomState) = common.setTabStatus(state)

    //방에 들어갈경우
    fun showRoomInfo(roomId: String) = common.showRoom(roomId)

    //방에서 나갈경우
    fun hideRoomInfo() = common.hideRoom()

    fun showDialog(dialogStatus: DialogStatus) = getDialogAction().showDialog(dialogStatus)
    fun hideDialog() = getDialogAction().hideDialog()

    val myRole
        get()= common.myRole
    var tempOfFBData
        get() = common.tempOfFBData
        set(value) {
            common.tempOfFBData = value
        }

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
                    common.checkByScheme(
                        roomId,
                        successEnter = {
                            _showRoomEnterByIntent.value = ""
                        },
                        successNeedPassword = { roomInfo ->
                            _showRoomEnterPasswordByIntent.value = Pair(roomId, roomInfo)
                        }
                    )
                }
            }
        }
    }


    val syncData get() = common.syncData
    fun syncStart(force: Boolean = false) = common.syncStart(user.value!!.uid!!, force)
    fun syncStartForce(uuid:String) = common.syncStart(uuid,true)
    fun signOut() = common.signOut()
    fun setNowUserInfo(userName: String) = common.setNowUserInfo(userName)
    fun setNowRaidInfo(raidId: String) = common.setNowRaidInfo(raidId)
    fun clearFocusItem() = common.clearFocusItem()

    var showSetting by mutableStateOf(false)
    val showLoading get() = common.showLoading
    val msg get() = common.msg
    fun updateCharacter(roomId: String,userInfo: UserInfo) = common.updateCharacter(roomId, userInfo)
    override fun getSetting() :Boolean = showSetting

    override fun closeSetting() {
        showSetting = false
    }

    fun bottomAddAction() = common.bottomAddAction()
    fun topBackAction() = common.topBackAction()

    suspend fun deleteRoom(roomId:String) = common.deleteRoom(roomId)

    fun getDialogAction() = common.dialogAction

    fun updatePartyFocusIndex(index:Int) = common.updatePartyFocusIndex(index)

    fun outOrSignOut() = common.outOrSignOut()

    val sheetSpace get() =  common.sheetSpace

    fun setSheetSpace(space:Float) = common.setSheetSpace(space)

}