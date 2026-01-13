@file:JvmName("LoginHelperJvm")
package com.wonddak.loacell.auth

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleLoginHelper
import com.wonddak.hellogin.google.GoogleOptionProviderAndroid
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.loacell.util.NameHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual class LoginHelper : GoogleOptionProviderAndroid {

    init {
        GoogleLoginHelper.setOptionProvider(this)
    }

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("631976126032-ujmhp8dm1gfndulkebm994lgqhgbrq7a.apps.googleusercontent.com")
        .build()

    override fun provideGoogleIdOption(): GetGoogleIdOption {
        return googleIdOption
    }

    actual val loginIn: MutableStateFlow<Boolean> = MutableStateFlow(false)
    actual val auth: FBAuth = FBAuth(Firebase.auth)

    actual fun registerTokenAction(
        result: GoogleResult,
        failAction: (msg: String) -> Unit,
        successAction: (credential: FBAuthCredential) -> Unit
    ) {
        val token = result.idToken
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
            successAction(FBAuthCredential(firebaseCredential))
        } catch (e: ApiException) {
            loginIn.value = false
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
//                    failAction("One-tap dialog was closed.")
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    failAction("One-tap encountered a network error.")
                }

                else -> {
                    failAction("Couldn't get credential from result. (${e.localizedMessage})")
                }
            }
        }
    }
    actual suspend fun requestGoogleLogin(
        tokenResultHandler: TokenResultHandler<GoogleResult>
    ) {
        GoogleLoginHelper.requestLogin(tokenResultHandler)
    }
}


actual class FBAuthCredential(
    val credential: AuthCredential
)

actual class FBAuth(
    private val auth: FirebaseAuth
) {
    private var _user: MutableStateFlow<FBUser?> = MutableStateFlow(null)

    actual val user: StateFlow<FBUser?>
        get() = _user

    init {
        auth.addAuthStateListener { auth ->
            _user.value = auth.currentUser?.let { FBUser(it) }
        }
    }

    actual fun signInWithCredential(
        credential: FBAuthCredential,
        failAction: () -> Unit,
        successAction: (result: FBAuthResult) -> Unit
    ) {
        auth.signInWithCredential(credential.credential)
            .addOnSuccessListener {
                successAction(FBAuthResult(it))
            }
            .addOnFailureListener {
                failAction()
            }
    }

    actual fun linkWithCredential(
        credential: FBAuthCredential,
        failAction: (msg: String) -> Unit,
        successAction: () -> Unit
    ) {
        auth.currentUser!!.linkWithCredential(credential.credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successAction()
                } else {
                    val error = task.exception
                    if (error is FirebaseAuthUserCollisionException) {
                        failAction("이미 등록된 계정입니다.")
                    } else {
                        failAction(error?.localizedMessage ?: "unknown Error")
                    }

                }
            }
    }

    actual fun signOut() {
        auth.signOut()
    }

    actual fun delete() {
        auth.currentUser!!.delete()
            .addOnSuccessListener {
                signOut()
            }
    }

    actual fun requestAnonymousLogin(successAction: () -> Unit) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                updateDisplayName(NameHelper.makeName())
                successAction()
            }
    }

    actual fun updateDisplayName(name: String) {
        auth.currentUser?.updateProfile(
            userProfileChangeRequest {
                displayName = name
            }
        )
    }

}

actual class FBUser(
    private val user: FirebaseUser
) {
    actual val uid: String
        get() = user.uid
    actual val displayName: String?
        get() = user.displayName
    actual val isAnonymous: Boolean
        get() = user.isAnonymous
    actual val photoUrl: String
        get() = user.photoUrl.toString()

    actual fun isAppleProviderExist(): Boolean {
        for (providerDatum in user.providerData) {
            if (providerDatum.providerId == "apple.com") {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "FBUser(uid='$uid', displayName=$displayName, isAnonymous=$isAnonymous, photoUrl='$photoUrl')"
    }
}

actual class FBAuthResult(
    private val result: AuthResult
) {
    actual val user: FBUser?
        get() = result.user?.let { FBUser(it) }
}