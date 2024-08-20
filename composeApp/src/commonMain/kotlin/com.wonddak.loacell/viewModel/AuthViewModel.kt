package com.wonddak.loacell.viewModel

class AuthViewModel(
    val loginHelper: LoginHelper,
) : ViewModel() {

    //현재 로그인된 유저 정보
    protected val userFlow get() = loginHelper.auth.user

}