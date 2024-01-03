package com.wonddak.loacell.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthCredential
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIROAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseCore.FIRApp
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
import com.wonddak.loacell.CommonMutableStateFlow
import com.wonddak.loacell.CommonStateFlow
import com.wonddak.loacell.toCommonMutableStateFlow
import com.wonddak.loacell.toCommonStateFlow
import com.wonddak.loacell.util.NameHelper
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.coroutines.flow.MutableStateFlow
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

actual class LoginHelper {
    actual val loginIn: CommonMutableStateFlow<Boolean> = MutableStateFlow(false).toCommonMutableStateFlow()

    actual val auth: FBAuth = FBAuth(FIRAuth.auth())

    actual fun registerTokenAction(
        result: GoogleResult,
        failAction: (msg:String) -> Unit,
        successAction: (credential: FBAuthCredential) -> Unit,
    ) {
        val user = result.user()
        val token = user.idToken?.tokenString
        if (token == null) {
            loginIn.value = false
        } else {
            val credential = FIRGoogleAuthProvider.credentialWithIDToken(
                IDToken = token,
                accessToken = user.accessToken.tokenString
            )
            successAction(FBAuthCredential(credential))
        }
    }
    fun requestGoogleLogin(
        successAction: (result:FBAuthResult) -> Unit
    ) {
        val presentingViewController = ((UIApplication.sharedApplication().connectedScenes()
            .first() as? UIWindowScene)?.windows() as List<UIWindow?>).first()?.rootViewController()
            ?: return
        val clientID = FIRApp.defaultApp()?.options?.clientID() ?: return
        val config = GIDConfiguration(clientID = clientID)
        GIDSignIn.sharedInstance().configuration = config
        GIDSignIn.sharedInstance()
            .signInWithPresentingViewController(presentingViewController = presentingViewController) { result, error ->
                if (result == null || error != null) {
                    return@signInWithPresentingViewController
                }

                registerGoogleToken(result,successAction = successAction)
            }
    }

    fun requestAnonymousToGoogleLogin(
        failAction: (msg: String) -> Unit,
        successAction: () -> Unit
    ) {
        val presentingViewController = ((UIApplication.sharedApplication().connectedScenes()
            .first() as? UIWindowScene)?.windows() as List<UIWindow?>).first()?.rootViewController()
            ?: return
        val clientID = FIRApp.defaultApp()?.options?.clientID() ?: return
        val config = GIDConfiguration(clientID = clientID)
        GIDSignIn.sharedInstance().configuration = config
        GIDSignIn.sharedInstance()
            .signInWithPresentingViewController(presentingViewController = presentingViewController) { result, error ->
                if (result == null || error != null) {
                    return@signInWithPresentingViewController
                }

                registerAnonymousToGoogle(result,failAction) {
                    successAction()
                }
            }
    }
}

@OptIn(BetaInteropApi::class)
fun LoginHelper.registerAppleToken(
    nonce: NSString,
    credential: ASAuthorizationAppleIDCredential,
    successAction: (FBAuthResult) -> Unit,
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
        IDToken = idTokenString.toString(),
        rawNonce = nonce.toString(),
        fullName = credential.fullName()
    )
    auth.signInWithCredential(FBAuthCredential(firebaseCredential),{ loginIn.value = false }) {
        loginIn.value = false
        successAction(it)
    }
}

fun LoginHelper.linkToApple() {

}


actual class FBAuthCredential(
    val credential: FIRAuthCredential
)
actual typealias GoogleResult = GIDSignInResult

actual class FBAuth(
    val auth: FIRAuth
) {
    private var _user: MutableStateFlow<FBUser?> = MutableStateFlow(null)

    actual val user: CommonStateFlow<FBUser?>
        get() = _user.toCommonStateFlow()

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
        auth.signInWithCredential(credential = credential.credential) {  FIRAuthDataResult, error ->
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
        auth.currentUser!!.linkWithCredential(credential.credential){ result, error ->
            if (error == null) {
                successAction()
            } else {
                failAction(error?.localizedDescription() ?: "unknown Error")
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
        auth.currentUser!!.deleteWithCompletion { error ->
            if (error == null) {
                signOut()
            } else {

            }
        }
    }

    actual fun requestAnonymousLogin() {
        auth.signInAnonymouslyWithCompletion { _, error ->
            if (error == null) {
                updateDisplayName(NameHelper.makeName())
            }
        }
    }

    actual fun updateDisplayName(name: String) {
        val cr = auth.currentUser?.profileChangeRequest()
        cr?.displayName = name
        cr?.commitChangesWithCompletion {

        }
    }

}

actual class FBUser(
    val user: FIRUser
) {
    actual val uid: String
        get() = user.uid
    actual val displayName: String?
        get() = user.displayName
    actual val photoUrl: String
        get() = user.photoURL.toString()
    actual val isAnonymous: Boolean
        get() = user.isAnonymous()
}


actual class FBAuthResult(
    val result : FIRAuthDataResult
) {
    actual val user: FBUser?
        get() = result.user?.let { FBUser(it) }

}