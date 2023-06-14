package com.wonddak.loacell.android.util

import android.content.Context
import android.content.IntentSender
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.wonddak.loacell.android.R


class LoginHelper(
    context: Context
)  {
    companion object {
        const val TAG = "LoginHelper"
    }

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    init {
        oneTapClient = Identity.getSignInClient(context)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
        auth = Firebase.auth
    }

    fun updateUserInfo(userInfo: FirebaseUser? = auth.currentUser) {
//        LoaCellApp.updateUser(userInfo)
    }

    fun signOut() {
        auth.signOut()
        updateUserInfo()
    }

    fun requestGoogleLogin(
        successAction: (IntentSenderRequest) -> Unit
    ) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    successAction(
                        IntentSenderRequest.Builder(
                            result.pendingIntent.intentSender
                        ).apply {
                            setFillInIntent(null)
                        }.build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                e.localizedMessage?.let { Log.d(TAG, it) }
            }
    }

    private fun registerToken(
        result: ActivityResult,
        authAction: (firebaseCredential: AuthCredential) -> Unit
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    Log.d(TAG, "Got ID token. $idToken")
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    authAction(firebaseCredential)
                }

                else -> {
                    // Shouldn't happen.
                    Log.d(TAG, "No ID token!")
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(TAG, "One-tap encountered a network error.")
                }

                else -> {
                    Log.d(TAG, "Couldn't get credential from result. (${e.localizedMessage})")
                }
            }
        }
    }

    fun registerGoogleToken(
        result: ActivityResult,
        commonAction :() ->Unit,
        otherAction: () -> Unit
    ) {
        registerToken(result) { firebaseCredential ->
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    commonAction()
                    if (task.isSuccessful) {
                        updateUserInfo()
                        otherAction()
                    }
                }
                .addOnFailureListener {
                    commonAction()
                }

        }
    }

    fun registerAnonymousToGoogle(
        result: ActivityResult,
        failAction: (e: Exception?) -> Unit
    ) {
        registerToken(result) { firebaseCredential ->
            auth.currentUser!!.linkWithCredential(firebaseCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")
                        updateUserInfo()
                    } else {
                        failAction(task.exception)
                    }
                }

        }
    }

    fun delete(
        successAction: () -> Unit
    ) {
        auth.currentUser!!.delete()
            .addOnSuccessListener {
                signOut()
                successAction()
            }
    }

    fun requestAnonymousLogin(
        name :String
    ) {
        auth.signInAnonymously()
            .addOnSuccessListener {
                updateUserInfo()
                updateDisplayName(name)
            }
    }

    fun updateDisplayName(name: String) {
        auth.currentUser?.updateProfile(
            userProfileChangeRequest {
                displayName = name
            }
        )
    }

    fun updateProfileImg(photoUri : Uri) {
        auth.currentUser?.updateProfile(
            userProfileChangeRequest {
                setPhotoUri(photoUri)
            }
        )
    }

}