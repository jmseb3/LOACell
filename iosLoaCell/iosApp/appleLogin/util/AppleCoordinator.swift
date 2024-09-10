//
//  AppleCoordinator.swift
//  iosApp
//
//  Created by WonHee Jung on 9/10/24.
//

import Foundation
import AuthenticationServices
import FirebaseAuth

fileprivate var currentNonce: String?

class AppleLinkCoordinator: AppleCoordinator {
    
    init(
        linkFailAction :@escaping (_ msg : String) -> Void,
        linkSuccessAction :@escaping () -> Void
    ) {
        super.init { appleIDCredential, nonce in
            guard let appleIDToken = appleIDCredential.identityToken else {
                print("Unable to fetch identity token")
                return
            }
            guard let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
                print("Unable to serialize token string from data: \(appleIDToken.debugDescription)")
                return
            }
            
            let credential = OAuthProvider.appleCredential(withIDToken: idTokenString,
                                                           rawNonce: nonce,
                                                           fullName: appleIDCredential.fullName)
            
            Auth.auth().currentUser?.link(with: credential) { (authResult, error) in
                if (error == nil) {
                    print("link success")
                    linkSuccessAction()
                } else {
                    print("link fail")
                    linkFailAction(error?.localizedDescription ?? "unknown error")
                }
            }
        } handleFail: { controller, error in
            print("link fail 22")

            if error is NSError {
                let error = (error as NSError)
                if error.code == 1001 {
                    return
                }
            }
            linkFailAction(error.localizedDescription ?? "unknwon error")
        }
    }
}

class AppleTokenRevokeCoordinator: AppleCoordinator {
    
    init(
        failAction :@escaping (_ msg : String) -> Void,
        revokeSuccessAction :@escaping () -> Void
    ) {
        super.init { appleIDCredential, nonce in
            guard let appleAuthCode = appleIDCredential.authorizationCode else {
                failAction("Unable to fetch authorization code")
                print("Unable to fetch authorization code")
                return
            }
            
            guard let authCodeString = String(data: appleAuthCode, encoding: .utf8) else {
                failAction("Unable to serialize auth code string from data: \(appleAuthCode.debugDescription)")
                print("Unable to serialize auth code string from data: \(appleAuthCode.debugDescription)")
                return
            }
            Task {
                do {
                    try await Auth.auth().revokeToken(withAuthorizationCode: authCodeString)
                    revokeSuccessAction()
                } catch {
                    failAction(error.localizedDescription ?? "unknwon error")
                }
            }
        } handleFail: { controller, error in
            failAction(error.localizedDescription ?? "unknwon error")
        }
    }
}

class AppleCoordinator: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {
    
    var handleSuccess : ( ASAuthorizationAppleIDCredential,_ nonce :String) -> Void
    var handleFail : ( ASAuthorizationController,Error) -> Void
    
    
    init(
        handleSuccess: @escaping (ASAuthorizationAppleIDCredential, _: String) -> Void,
        handleFail: @escaping (ASAuthorizationController, Error) -> Void
    ) {
        self.handleSuccess = handleSuccess
        self.handleFail = handleFail
    }
    
    func startLogin() {
        print("start Login...")
        let nonce = AppleLoginHelper().randomNonceString()
        currentNonce = nonce
        
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = AppleLoginHelper().sha256(nonce)
        
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        print("123")
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
            print("456")
            guard let nonce = currentNonce else {
                fatalError("Invalid state: A login callback was received, but no login request was sent.")
            }
            handleSuccess(appleIDCredential,nonce)
        }
    }
    
    // Apple ID 연동 실패 시
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        print("error >> ")
        // Handle error.
        handleFail(controller,error)
    }
    
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return UIApplication.shared.windows.filter { $0.isKeyWindow }.first!
    }
    
   
}
