//
//  AppleSiginCoordinator.swift
//  iosApp
//
//  Created by WonHee Jung on 1/16/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import AuthenticationServices
import FirebaseAuth

struct WindowKey: EnvironmentKey {
    struct Value {
        weak var value: UIWindow?
    }
    
    static let defaultValue: Value = .init(value: nil)
}

extension EnvironmentValues {
    var window: UIWindow? {
        get {
            return self[WindowKey.self].value
        }
        set {
            self[WindowKey.self] = .init(value: newValue)
        }
    }
}

fileprivate var currentNonce: String?

class AppleLinkCoordinator: AppleCoordinator {
    
    init(
        window: UIWindow?,
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
                    linkSuccessAction()
                } else {
                    linkFailAction(error?.localizedDescription ?? "unknown error")
                }
            }
        } handleFail: { controller, error in
            linkFailAction(error.localizedDescription ?? "unknwon error")
        }
    }
}

class AppleTokenRevokeCoordinator: AppleCoordinator {
    
    init(
        window: UIWindow?,
        failAction :@escaping (_ msg : String) -> Void,
        revokeSuccessAction :@escaping () -> Void
    ) {
        super.init { appleIDCredential, nonce in
            guard let appleAuthCode = appleIDCredential.authorizationCode else {
                failAction("Unable to fetch authorization code")
                return
            }
            
            guard let authCodeString = String(data: appleAuthCode, encoding: .utf8) else {
                failAction("Unable to serialize auth code string from data: \(appleAuthCode.debugDescription)")
                return
            }
            Task {
                do {
                    try await Auth.auth().revokeToken(withAuthorizationCode: authCodeString)
                    revokeSuccessAction()
                } catch {
                    print(111)
                    print(error)
                    failAction(error.localizedDescription ?? "unknwon error")
                }
            }
        } handleFail: { controller, error in
            print(123)
            print(error)
            failAction(error.localizedDescription ?? "unknwon error")
        }
    }
}

class AppleCoordinator: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {
    
    var window: UIWindow?
    var handleSuccess : ( ASAuthorizationAppleIDCredential,_ nonce :String) -> Void
    var handleFail : ( ASAuthorizationController,Error) -> Void
    
    
    init(window: UIWindow? = nil, handleSuccess: @escaping (ASAuthorizationAppleIDCredential, _: String) -> Void, handleFail: @escaping (ASAuthorizationController, Error) -> Void) {
        self.window = window
        self.handleSuccess = handleSuccess
        self.handleFail = handleFail
    }
    
    func startLogin() {
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
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
            guard let nonce = currentNonce else {
                fatalError("Invalid state: A login callback was received, but no login request was sent.")
            }
            handleSuccess(appleIDCredential,nonce)
        }
    }
    
    // Apple ID 연동 실패 시
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle error.
        handleFail(controller,error)
    }
    
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return window!
    }
}
