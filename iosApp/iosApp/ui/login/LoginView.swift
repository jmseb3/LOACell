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
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    let loginHelper = LoginHelper.instance
    
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
                    Text(CommonString.Login().getAnonymous().localized())
                }
            }
            VStack {
                if (viewModel.logginIn) {
                    LoadingView(info: CommonString.Login().getProgress().localized())
                }
            }
        }
        
    }
    
}

#Preview {
    LoginView().environmentObject(LoaCellViewModel())
}
