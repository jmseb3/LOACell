package com.wonddak.loacell.auth

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.CommonStateFlow
import com.wonddak.loacell.toCommonStateFlow
import com.wonddak.loacell.util.NameHelper
import kotlinx.coroutines.flow.MutableStateFlow

actual class LoginHelper(
    context: Context
) {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private var _loginIn: MutableStateFlow<Boolean> = MutableStateFlow(false)

    actual val loginIn: CommonStateFlow<Boolean>
        get() = _loginIn.toCommonStateFlow()

    actual val auth: FBAuth = FBAuth(Firebase.auth)

    init {
        oneTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("631976126032-ujmhp8dm1gfndulkebm994lgqhgbrq7a.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    actual fun signOut() {
        auth.signOut()
    }

    actual fun delete() {
        auth.delete()
    }

    actual fun registerToken(
        result: GoogleResult,
        failAction: (msg: String) -> Unit,
        successAction: (credential: FBAuthCredential) -> Unit
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            if (idToken == null) {
                _loginIn.value = false
            } else {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                successAction(FBAuthCredential(firebaseCredential))
            }
        } catch (e: ApiException) {
            _loginIn.value = false
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

    fun requestGoogleLogin(
        launcher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    val intentSender =
                        IntentSenderRequest.Builder(
                            result.pendingIntent.intentSender
                        ).apply {
                            setFillInIntent(null)
                        }.build()
                    launcher.launch(intentSender)

                } catch (e: IntentSender.SendIntentException) {
                    println("Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                e.localizedMessage?.let { println(it) }
            }
    }

    actual fun registerGoogleToken(
        result: GoogleResult,
        successAction: () -> Unit,
    ) {
        _loginIn.value = true
        registerToken(
            result,
            {}
        ) { credential ->
            auth.signInWithCredential(credential, { _loginIn.value = false }) {
                _loginIn.value = true
                successAction()
            }
        }
    }

    actual fun registerAnonymousToGoogle(
        result: GoogleResult,
        failAction: (msg: String) -> Unit
    ) {
        registerToken(result, failAction) { credential ->
            auth.linkWithCredential(credential, failAction) {

            }
        }
    }

}
actual class FBAuthCredential(
    val credential: AuthCredential
)
actual typealias GoogleResult = ActivityResult


actual class FBAuth(
    private val auth: FirebaseAuth
) {
    private var _user: MutableStateFlow<FBUser?> = MutableStateFlow(null)

    actual val user: CommonStateFlow<FBUser?>
        get() = _user.toCommonStateFlow()

    init {
        auth.addAuthStateListener {fAtuh ->
            _user.value = fAtuh.currentUser?.let { it -> FBUser(it) }
        }
    }

    actual fun signInWithCredential(
        credential: FBAuthCredential,
        failAction: () -> Unit,
        successAction: () -> Unit
    ) {
        auth.signInWithCredential(credential.credential)
            .addOnSuccessListener {
                successAction()
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

    actual fun requestAnonymousLogin() {
        auth.signInAnonymously()
            .addOnSuccessListener {
                updateDisplayName(NameHelper.makeName())
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
    actual val isAnonymous : Boolean
        get() = user.isAnonymous
    actual val photoUrl: String
        get() = user.photoUrl.toString()
}