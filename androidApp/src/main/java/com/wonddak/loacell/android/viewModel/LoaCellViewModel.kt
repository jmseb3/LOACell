package com.wonddak.loacell.android.viewModel


import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.database.AppDataBase
import com.wonddak.loacell.CommonViewModel
import com.wonddak.loacell.Config
import com.wonddak.loacell.ILOA
import com.wonddak.loacell.RoomInfo
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.ext.TotalRoomInfo
import com.wonddak.loacell.storage.SynergyReferenceHelper
import kotlinx.coroutines.launch

class LoaCellViewModel(
    dataBase: AppDataBase,
    config: Config,
    loginHelper: LoginHelper,
    synergyReferenceHelper: SynergyReferenceHelper
) : CommonViewModel(dataBase, config, loginHelper,synergyReferenceHelper) {

    //region snackbar
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
    //endregion

    var showSetting by mutableStateOf(false)
    override fun getSetting(): Boolean = showSetting

    override fun closeSetting() {
        showSetting = false
    }

    var user: FBUser? by mutableStateOf(null)
        private  set
    var roomList: List<RoomInfo> by mutableStateOf(emptyList())
        private  set
    var roomId: String by mutableStateOf("")
        private  set
    var totalRoomInfoValue: TotalRoomInfo by mutableStateOf(TotalRoomInfo.getInit())
        private set

    var syncData: Boolean by mutableStateOf(false)
        private  set
    var showLoading: Boolean by mutableStateOf(false)
        private  set
    var msg: String by mutableStateOf("")
        private  set
    var sheetSpace: Float by mutableFloatStateOf(20f)
        private  set
    var defaultUrl: String by mutableStateOf(ILOA)
        private  set
    init {
        viewModelScope.launch {
            userFlow.collect {
                user = it
            }
        }
        viewModelScope.launch {
            roomListFlow.collect {
                roomList = it
            }
        }
        viewModelScope.launch {
            roomIdFlow.collect {
                roomId = it
            }
        }
        viewModelScope.launch {
            totalRoomInfoFlow.collect {
                totalRoomInfoValue = it
            }
        }
        viewModelScope.launch {
            syncDataFlow.collect {
                syncData = it
            }
        }
        viewModelScope.launch {
            showLoadingFlow.collect {
                showLoading = it
            }
        }
        viewModelScope.launch {
            msgFlow.collect {
                msg = it
            }
        }
        viewModelScope.launch {
            sheetSpaceFlow.collect {
                sheetSpace = it
            }
        }
        viewModelScope.launch {
            defaultUrlFlow.collect {
                defaultUrl = it
            }
        }
    }
}