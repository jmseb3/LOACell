//
//  LoginView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import GoogleSignInSwift
import shared

struct LoginView: View {
    @ObservedObject private(set) var viewModel: LoaCellViewModel
    
    let loginHelper = LoginHelper.instance
    
    var body: some View {
        VStack {
            Spacer()
            HStack {
                Text("레이드 관리를 도와주는")
                Image(uiImage: SharedRes.images().logo.toUIImage()!)
                Text("입니다")
            }
            Spacer()
            GoogleSignInButton(action: {
                loginHelper.requestGoogleLogin() {
                    viewModel.syncStart(force: true)
                }
            })
            Button(action: {loginHelper.requestAnonymousLogin()}) {
                Text("로그인 하지 않고 계속")
            }
        }
    }
    
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(viewModel: LoaCellViewModel())
    }
}
