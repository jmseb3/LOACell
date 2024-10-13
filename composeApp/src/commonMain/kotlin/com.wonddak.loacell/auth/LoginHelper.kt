package com.wonddak.loacell.auth


import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect class LoginHelper(){
    val loginIn : MutableStateFlow<Boolean>
    val auth : FBAuth

    fun registerTokenAction(
        result : GoogleResult,
        failAction: (msg:String) -> Unit,
        successAction: (credential : FBAuthCredential) -> Unit,
    )

    suspend fun requestGoogleLogin(
        tokenResultHandler: TokenResultHandler<GoogleResult>
    )
}

fun LoginHelper.registerGoogleToken(
    result: GoogleResult
) {
    loginIn.value = true
    registerTokenAction(result, {}) { credential ->
        auth.signInWithCredential(credential, { loginIn.value = false }) {
            loginIn.value = false
        }
    }
}

fun LoginHelper.registerAnonymousToGoogle(
    result: GoogleResult,
    failAction: (msg:String) -> Unit,
    successAction: () -> Unit
) {
    registerTokenAction(result, failAction) { credential ->
        auth.linkWithCredential(credential, failAction) {
            successAction()
        }
    }
}

fun LoginHelper.signOut() {
    this.auth.signOut()
}

fun LoginHelper.delete() {
    this.auth.delete()
}

fun LoginHelper.requestAnonymousLogin() {
    loginIn.value = true
    this.auth.requestAnonymousLogin {
        loginIn.value = false
    }
}

expect class FBAuthCredential

expect class FBAuth {
    val user: StateFlow<FBUser?>
    fun signInWithCredential(
        credential: FBAuthCredential,
        failAction: () -> Unit,
        successAction: (result:FBAuthResult) -> Unit
    )

    fun linkWithCredential(
        credential: FBAuthCredential,
        failAction: (msg:String) -> Unit,
        successAction: () -> Unit
    )
    fun signOut()
    fun delete()

    fun requestAnonymousLogin(successAction: () -> Unit)

    fun updateDisplayName(name:String)
}

expect class FBUser {
    val uid :String
    val displayName :String?
    val photoUrl : String
    val isAnonymous :Boolean

    fun isAppleProviderExist(): Boolean
}

expect class FBAuthResult {

    val user :FBUser?
}