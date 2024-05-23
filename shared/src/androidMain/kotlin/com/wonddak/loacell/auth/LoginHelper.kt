@file:JvmName("LoginHelperJvm")
package com.wonddak.loacell.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.CommonMutableStateFlow
import com.wonddak.loacell.CommonStateFlow
import com.wonddak.loacell.toCommonMutableStateFlow
import com.wonddak.loacell.toCommonStateFlow
import com.wonddak.loacell.util.NameHelper
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.java.KoinJavaComponent

actual class LoginHelper {
    private val context : Context = KoinJavaComponent.getKoin().get()

    private val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("631976126032-ujmhp8dm1gfndulkebm994lgqhgbrq7a.apps.googleusercontent.com")
        .build()

    private val credentialManager  by lazy {
        CredentialManager.create(context)
    }

    actual val loginIn: CommonMutableStateFlow<Boolean> = MutableStateFlow(false).toCommonMutableStateFlow()

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

    suspend fun requestGoogleLogin(
        activity :Activity,
        successAction: (result: FBAuthResult) -> Unit,
    ) {
        startGoogleLogin(activity) { cred ->
            registerGoogleToken(cred) {
                successAction(it)
            }
        }
    }

    suspend fun linkToGoogle(
        activity :Activity,
        failAction: (msg: String) -> Unit,
        successAction: () -> Unit
    ) {
        startGoogleLogin(activity) { cred ->
            registerAnonymousToGoogle(cred,failAction) {
                successAction()
            }
        }
    }
    private suspend fun startGoogleLogin(
        activity :Activity,
        successAction: (result: GoogleIdTokenCredential) -> Unit
    ) {
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        runCatching {
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            val credential = result.credential

            when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            // Use googleIdTokenCredential and extract id to validate and
                            // authenticate on your server.
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            successAction(googleIdTokenCredential)
                        } catch (e: GoogleIdTokenParsingException) {
//                            Log.e(TAG, "Received an invalid google id token response", e)
                        }
                    } else {
                        // Catch any unrecognized custom credential type here.
//                        Log.e(TAG, "Unexpected type of credential")
                    }
                }

                else -> {
                    // Catch any unrecognized credential type here.
//                    Log.e(TAG, "Unexpected type of credential")
                }
            }
        }.onFailure { e ->
            e.printStackTrace()
        }.onSuccess {
            println("Login2 Success")
        }
    }
}


actual class FBAuthCredential(
    val credential: AuthCredential
)
actual typealias GoogleResult = GoogleIdTokenCredential

actual class FBAuth(
    private val auth: FirebaseAuth
) {
    private var _user: MutableStateFlow<FBUser?> = MutableStateFlow(null)

    actual val user: CommonStateFlow<FBUser?>
        get() = _user.toCommonStateFlow()

    init {
        auth.addAuthStateListener { fAtuh ->
            _user.value = fAtuh.currentUser?.let { it -> FBUser(it) }
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
    val user: FirebaseUser
) {
    actual val uid: String
        get() = user.uid
    actual val displayName: String?
        get() = user.displayName
    actual val isAnonymous: Boolean
        get() = user.isAnonymous
    actual val photoUrl: String
        get() = user.photoUrl.toString()
}

actual class FBAuthResult(
    val result: AuthResult
) {
    actual val user: FBUser?
        get() = result.user?.let { FBUser(it) }
}