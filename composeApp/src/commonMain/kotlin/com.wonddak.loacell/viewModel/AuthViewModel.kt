package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.auth.LoginHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel(
    val loginHelper: LoginHelper,
) : ViewModel() {

    //현재 로그인된 유저 정보
    var user: FBUser? by mutableStateOf(null)
        private set

    var initSuccess: Boolean by mutableStateOf(false)
        private set

    var loginIn by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            loginHelper.loginIn.collect {
                loginIn = it
            }
        }
        viewModelScope.launch {
            loginHelper.auth.user.collect {
                user = it
                initSuccess = true
            }
        }
    }

}