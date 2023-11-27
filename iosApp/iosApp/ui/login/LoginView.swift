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
                GoogleSignInButton() {
                    loginHelper.requestGoogleLogin(successAction: { result in
                        self.loginIn = false
                        viewModel.syncStartForce(uuid: result.user!.uid)
                    })
                }
                Button(action: {loginHelper.auth.requestAnonymousLogin()}) {
                    Text(CommonString.Login().getAnonymous().localized())
                }
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
