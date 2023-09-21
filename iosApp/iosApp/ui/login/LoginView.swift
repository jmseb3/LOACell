//
//  LoginView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import GoogleSignInSwift

struct LoginView: View {
    @ObservedObject private(set) var viewModel: LoaCellViewModel
    
    let loginHelper = LoginHelper.instance
    
    var body: some View {
        VStack {
            Text("Hello, World!")
            GoogleSignInButton(action: {
                loginHelper.requestGoogleLogin() {
                    viewModel.syncStart(force: true)
                }
            })
        }
    }
    
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView(viewModel: LoaCellViewModel())
    }
}
