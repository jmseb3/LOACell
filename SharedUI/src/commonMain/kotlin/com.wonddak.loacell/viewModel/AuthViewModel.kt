package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.hellogin.core.Error
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.loacell.auth.FBUser
import com.wonddak.loacell.auth.LoginHelper
import com.wonddak.loacell.auth.delete
import com.wonddak.loacell.auth.registerAnonymousToGoogle
import com.wonddak.loacell.auth.registerGoogleToken
import com.wonddak.loacell.auth.requestAnonymousLogin
import com.wonddak.loacell.auth.signOut
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class AuthViewModel(
    val loginHelper: LoginHelper,
) : ViewModel() {

    //현재 로그인된 유저 정보
    var user: FBUser? by mutableStateOf(null)
        private set

    //user 정보가 최초 1번 불러와졌는지 여부
    var initSuccess: Boolean by mutableStateOf(false)
        private set

    //로그인 중인지 여부
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
                initSuccess = true
                user = it
            }
        }
    }

    fun updateName(name: String) {
        loginHelper.auth.updateDisplayName(name)
    }

    fun outOrSignOut() {
        user?.let {
            if (it.isAnonymous) {
                loginHelper.delete()
            } else {
                loginHelper.signOut()
            }
        }
    }

    fun deleteAccount() {
        loginHelper.delete()
    }

    fun launchAnonymousLogin() {
        viewModelScope.launch {
            loginHelper.requestAnonymousLogin()
        }
    }

    private val googleLoginHandler = object : TokenResultHandler<GoogleResult> {
        override fun onFail(error: Error?) {
            Napier.d(tag = "auth") { "fail with $error" }
        }

        override fun onSuccess(token: GoogleResult) {
            Napier.d(tag = "auth") { "success with $token" }
            viewModelScope.launch {
                loginHelper.registerGoogleToken(token)
            }
        }
    }

    fun requestGoogleLogin() {
        viewModelScope.launch {
            loginHelper.requestGoogleLogin(googleLoginHandler)
        }
    }

    fun linkToGoogleAccount(
        failAction: (String) -> Unit,
        successAction: () -> Unit
    ) {
        val googleLinkHandler = object : TokenResultHandler<GoogleResult> {
            override fun onFail(error: Error?) {
                failAction(error?.toString() ?: "unknown error")
            }

            override fun onSuccess(token: GoogleResult) {
                loginHelper.registerAnonymousToGoogle(
                    token,
                    failAction,
                    successAction
                )
            }
        }
        viewModelScope.launch {
            loginHelper.requestGoogleLogin(googleLinkHandler)
        }
    }

    fun checkAppleProvider() = user?.isAppleProviderExist() ?: false
}
