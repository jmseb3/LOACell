package com.wonddak.loacell.auth

import com.wonddak.loacell.CommonStateFlow

expect class LoginHelper {
    val loginIn : CommonStateFlow<Boolean>
    val auth : FBAuth
    fun signOut()
    fun delete()

    fun registerToken(
        result : GoogleResult,
        failAction: (msg:String) -> Unit,
        successAction: (credential : FBAuthCredential) -> Unit,
    )

    fun registerGoogleToken(
        result : GoogleResult,
        successAction: (FBAuthResult) -> Unit,
    )
    fun registerAnonymousToGoogle(
        result: GoogleResult,
        failAction: (msg:String) -> Unit
    )
}

expect class FBAuthCredential
expect class GoogleResult

expect class FBAuth {
    val user: CommonStateFlow<FBUser?>
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

    fun requestAnonymousLogin()

    fun updateDisplayName(name:String)
}

expect class FBUser {
    val uid :String
    val displayName :String?
    val photoUrl : String
    val isAnonymous :Boolean
}

expect class FBAuthResult {

    val user :FBUser?
}