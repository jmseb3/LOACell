//
//  LoginInfoView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import FirebaseAuth

struct LoginInfoView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var user : FBUser?  {
        viewModel.user
    }
    var loginHelper : LoginHelper {
        self.viewModel.loginHelper
    }
    
    var body: some View {
        VStack {
            if let userInfo = user {
                HStack {
                    VStack(alignment: .leading) {
                        Text((userInfo.displayName ?? "").ifEmpty{ "이름 없음"})
                            .fontWeight(.bold)
                        Text(userInfo.uid)
                    }
                    IconButton(resource: \.change_person) {
                        viewModel.showDialog(dialogStatus: DialogStatus.settingEditName)
                    }
                }
                Divider()
                
                VStack {
                    HStack {
                        RoundCornerButton(text: userInfo.isAnonymous ? "나가기" : "로그아웃") {
                            viewModel.outOrSignOut()
                        }
                        if !userInfo.isAnonymous {
                            Spacer()
                                .frame(width: 15)
                            RoundCornerButton(text: "탈퇴") {
                                loginHelper.delete()
                            }
                        }
                    }
                    if userInfo.isAnonymous {
                        RoundCornerButton(text: "Google 계정 연동" ) {
                            loginHelper.requestAnonymousToGoogleLogin(
                                failAction: { msg in
                                    viewModel.showSnackBar(msg: msg)
                                },successAction: {
                                    
                                }
                            )
                        }
                    }
                }
                .frame(maxWidth: .infinity)
                .padding(5)
            }
        }
    }
}

