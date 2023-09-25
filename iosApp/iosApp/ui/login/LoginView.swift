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
        ZStack {
            VStack {
                Spacer()
                HStack {
                    Text("레이드 관리를 도와주는")
                    Image(uiImage: SharedRes.images().logo.toUIImage()!)
                    Text("입니다")
                }
                Spacer()
                GoogleSignInButton(action: {
                    loginHelper.requestGoogleLogin(
                        commonAction : {
                            viewModel.logginIn = true
                        },
                        successAction: {
                            viewModel.syncStart(force: true)
                            viewModel.logginIn = false
                        },
                        failAction: {
                            viewModel.logginIn = false
                        }
                    )
                })
                Button(action: {loginHelper.requestAnonymousLogin()}) {
                    Text("로그인 하지 않고 계속")
                }
            }
            VStack {
                if (viewModel.logginIn) {
                    LoadingView(info: "로그인 처리 중입니다.")
                }
            }
        }
        
    }
    
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(viewModel: LoaCellViewModel())
    }
}
