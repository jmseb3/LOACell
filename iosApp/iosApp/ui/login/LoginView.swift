//
//  LoginView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import GoogleSignInSwift
import AuthenticationServices
import CryptoKit

struct LoginView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    @State private var nonce :String = ""
    let appleHelper : AppleLoginHelper = AppleLoginHelper()
    
    var loginHelper : LoginHelper {
        self.viewModel.loginHelper
    }
    @State var loginIn = false
    
    var body: some View {
        ZStack {
            VStack {
                Spacer()
                HStack {
                    Text(CommonString.Login().getInfo1().localized())
                    Image(resource: \.logo)
                    Text(CommonString.Login().getInfo2().localized())
                }
                Spacer()
                
                VStack {
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
                            viewModel.loginHelper.registerAppleToken(nonce: nonce, credential: credential) { result in
                                self.loginIn = false
                                viewModel.syncStartForce(uuid: result.user!.uid)
                            }
                        case .failure(let error):
                            print(error.localizedDescription)
                        }
                    }
                    .frame(height:50)
                    .cornerRadius(5)
                    
                    GoogleSignInButton(
                        viewModel: GoogleSignInButtonViewModel(
                            style: GoogleSignInButtonStyle.wide
                        )
                    ) {
                        loginHelper.requestGoogleLogin(successAction: { result in
                            self.loginIn = false
                            viewModel.syncStartForce(uuid: result.user!.uid)
                        })
                    }
                    Button(action: {loginHelper.requestAnonymousLogin()}) {
                        Text(CommonString.Login().getAnonymous().localized())
                    }
                }
                .padding(.horizontal)
            }.onAppear {
                loginHelper.loginIn.collect { value in
                    self.loginIn = value as! Bool
                }
            }
            VStack {
                if (loginIn) {
                    LoadingView(info: CommonString.Login().getProgress().localized())
                }
            }
        }
    }
}

#Preview {
    LoginView().environmentObject(LoaCellViewModel())
}
