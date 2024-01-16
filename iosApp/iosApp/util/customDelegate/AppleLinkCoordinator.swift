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

class AppleLinkCoordinator: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {
    
    let window: UIWindow?
    let linkFailAction :(_ msg : String) -> Void
    let linkSuccessAction :() -> Void
    
    init(
        window: UIWindow?,
        linkFailAction :@escaping (_ msg : String) -> Void,
        linkSuccessAction :@escaping () -> Void
    ) {
        self.window = window
        self.linkFailAction = linkFailAction
        self.linkSuccessAction = linkSuccessAction
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
                    self.linkSuccessAction()
                } else {
                    self.linkFailAction(error?.localizedDescription ?? "unknown error")
                }
            }
        }
    }
    
    // Apple ID 연동 실패 시
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle error.
        self.linkFailAction(error.localizedDescription ?? "unknown error")
    }
    
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return window!
    }
}
