//
//  AppleSignInButton.swift
//  iosApp
//
//  Created by WonHee Jung on 9/10/24.
//

import SwiftUI
import ComposeApp
import AuthenticationServices
import CryptoKit

struct AppleSignInButton: View {
    @State private var nonce :String = ""
    let appleHelper : AppleLoginHelper = AppleLoginHelper()
    let loginHelper = LoginHelperIn()
    var body: some View {
      
        SignInWithAppleButton { (request) in
            nonce = appleHelper.randomNonceString()
            request.requestedScopes = [.email,.fullName]
            request.nonce = appleHelper.sha256(nonce)
        } onCompletion: { (result) in
            switch result {
            case .success(let user):
                print("success")
                guard let credential = user.credential as? ASAuthorizationAppleIDCredential else {
                    print("error with firebase")
                    return
                }
                loginHelper.registerAppleToken(nonce: nonce, credential: credential)
            case .failure(let error):
                print(error.localizedDescription)
            }
        }
    }
}

#Preview {
    AppleSignInButton()
}
