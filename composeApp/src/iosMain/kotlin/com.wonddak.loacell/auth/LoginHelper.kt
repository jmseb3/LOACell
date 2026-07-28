@file:OptIn(ExperimentalForeignApi::class)

package com.wonddak.loacell.auth

import swiftPMImport.LoaCell.composeApp.FIRAuth
import swiftPMImport.LoaCell.composeApp.FIRAuthCredential
import swiftPMImport.LoaCell.composeApp.FIRAuthDataResult
import swiftPMImport.LoaCell.composeApp.FIRGoogleAuthProvider
import swiftPMImport.LoaCell.composeApp.FIROAuthProvider
import swiftPMImport.LoaCell.composeApp.FIRUser
import cocoapods.GoogleSignIn.GIDGoogleUser
import com.wonddak.hellogin.apple.AppleLoginHelper
import com.wonddak.hellogin.apple.AppleOptionProvider
import com.wonddak.hellogin.apple.AppleSignInRequestScope
import com.wonddak.hellogin.core.TokenResultHandler
import com.wonddak.hellogin.google.GoogleLoginHelper
import com.wonddak.hellogin.google.GoogleResult
import com.wonddak.hellogin.google.setEmptyOption
import com.wonddak.loacell.util.NameHelper
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create

actual class LoginHelper : AppleOptionProvider{

    init {
        GoogleLoginHelper.setEmptyOption()
        AppleLoginHelper.setOptionProvider(this)
    }
    actual val loginIn: MutableStateFlow<Boolean> = MutableStateFlow(false)

    actual val auth: FBAuth = FBAuth(FIRAuth.auth())

    actual fun  registerTokenAction(
        result: GoogleResult,
        failAction: (msg:String) -> Unit,
        successAction: (credential: FBAuthCredential) -> Unit,
    ) {
        val user : GIDGoogleUser = result.user()
        val token = user.idToken?.tokenString
        if (token == null) {
            loginIn.value = false
        } else {
            val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                idToken = token,
                accessToken = user.accessToken.tokenString
            )
            successAction(FBAuthCredential(credential))
        }
    }

    actual suspend fun requestGoogleLogin(
        tokenResultHandler: TokenResultHandler<GoogleResult>
    ) {
        GoogleLoginHelper.requestLogin(tokenResultHandler)
    }

    override val requestScope: AppleSignInRequestScope
        get() = AppleSignInRequestScope.FullNameAndEmail
}

@OptIn(BetaInteropApi::class)
fun LoginHelper.registerAppleToken(
    nonce: String?,
    credential: ASAuthorizationAppleIDCredential,
) {
    loginIn.value = true
    val appleIDToken = credential.identityToken()
    if (appleIDToken == null) {
        loginIn.value = false
        println("error with firebase")
        return
    }

    val idTokenString = NSString.create(appleIDToken, NSUTF8StringEncoding)

    if (idTokenString == null) {
        loginIn.value = false
        println("error with token")
        return
    }

    val firebaseCredential = FIROAuthProvider.appleCredentialWithIDToken(
        idToken = idTokenString.toString(),
        rawNonce = nonce.toString(),
        fullName = credential.fullName()
    )
    auth.signInWithCredential(FBAuthCredential(firebaseCredential),{ loginIn.value = false }) {
        loginIn.value = false
    }
}

@OptIn(BetaInteropApi::class)
fun LoginHelper.registerAnonymousToApple(
    nonce: String?,
    credential: ASAuthorizationAppleIDCredential,
    onSuccess: () -> Unit,
    onFail: (msg: String) -> Unit
) {
    val appleIDToken = credential.identityToken()
    if (appleIDToken == null) {
        onFail("error with firebase")
        return
    }

    val idTokenString = NSString.create(appleIDToken, NSUTF8StringEncoding)

    if (idTokenString == null) {
        onFail("error with token")
        return
    }

    val firebaseCredential = FIROAuthProvider.appleCredentialWithIDToken(
        idToken = idTokenString.toString(),
        rawNonce = nonce.toString(),
        fullName = credential.fullName()
    )
    auth.linkWithCredential(FBAuthCredential(firebaseCredential), onFail, onSuccess)
}

@OptIn(BetaInteropApi::class)
fun LoginHelper.revokeAppleUser(
    credential: ASAuthorizationAppleIDCredential,
    onSuccess: () -> Unit
) {
    credential.authorizationCode?.let { data ->
        NSString.create(data, NSUTF8StringEncoding)?.let { codeString ->
            auth.revokeToken(codeString) {
                this.delete()
                onSuccess()
            }
        }
    }
}


actual class FBAuthCredential(
    val credential: FIRAuthCredential
)

actual class FBAuth(
    val auth: FIRAuth
) {
    private var _user: MutableStateFlow<FBUser?> = MutableStateFlow(null)

    actual val user: StateFlow<FBUser?>
        get() = _user

    init {
        auth.addAuthStateDidChangeListener { _, firUser ->
            _user.value = firUser?.let { FBUser(it) }
        }
    }


    actual fun signInWithCredential(
        credential: FBAuthCredential,
        failAction: () -> Unit,
        successAction: (result:FBAuthResult) -> Unit
    ) {
        Napier.d(tag = "auth") { "[2] signInWithCredential" }
        auth.signInWithCredential(credential = credential.credential) {  FIRAuthDataResult, error ->
            Napier.d(tag = "auth") { "[$FIRAuthDataResult] /$error]" }
            if (error == null) {
                successAction(FBAuthResult(FIRAuthDataResult!!))
            } else {
                failAction()
            }
        }
    }
    actual fun linkWithCredential(
        credential: FBAuthCredential,
        failAction: (msg: String) -> Unit,
        successAction: () -> Unit
    ) {
        auth.currentUser()?.linkWithCredential(credential.credential){ result, error ->
            if (error == null) {
                successAction()
            } else {
                failAction(error.localizedDescription())
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun signOut() {
        val error: CPointer<ObjCObjectVar<NSError?>>? = null
        auth.signOut(error).let { result ->
            if (!result) {

            }
        }
    }

    actual fun delete() {
        auth.currentUser()?.deleteWithCompletion { error ->
            if (error == null) {
                signOut()
            }
        }
    }

    actual fun requestAnonymousLogin(
        successAction: () -> Unit
    ) {
        auth.signInAnonymouslyWithCompletion { _, error ->
            if (error == null) {
                updateDisplayName(NameHelper.makeName())
                successAction()
            }
        }
    }

    actual fun updateDisplayName(name: String) {
        val cr = auth.currentUser()?.profileChangeRequest()
        cr?.setDisplayName(name)
        cr?.commitChangesWithCompletion {

        }
    }

    @OptIn(BetaInteropApi::class)
    fun revokeToken(
        authCode: NSString,
        complete: (NSError?) -> Unit
    ) {
        auth.revokeTokenWithAuthorizationCode(authCode.toString(), complete)
    }

}

actual class FBUser(
    private val user: FIRUser
) {
    actual val uid: String
        get() = user.uid()
    actual val displayName: String?
        get() = user.displayName()
    actual val photoUrl: String
        get() = user.photoURL().toString()
    actual val isAnonymous: Boolean
        get() = user.isAnonymous()

    actual fun isAppleProviderExist(): Boolean {
        for (providerDatum in user.providerData()) {
            if (providerDatum == "apple.com") {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "FBUser(uid='$uid', displayName=$displayName, photoUrl='$photoUrl', isAnonymous=$isAnonymous)"
    }
}


actual class FBAuthResult(
    private val result: FIRAuthDataResult
) {
    actual val user: FBUser?
        get() = FBUser(result.user())
}
