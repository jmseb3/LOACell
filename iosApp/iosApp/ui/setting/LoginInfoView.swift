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
                
                HStack {
                    Button(action: {
                        viewModel.outOrSignOut()
                    }, label: {
                        Text(userInfo.isAnonymous ? "나가기" : "로그아웃")
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(.black, lineWidth: 1)
                            )
                    })
                    Spacer()
                        .frame(width: 15)
                    Button(action: {
                        if userInfo.isAnonymous {
                            loginHelper.requestAnonymousToGoogleLogin(failAction: { msg in
                                viewModel.showSnackBar(msg: msg)
                            })
                        } else {
                            loginHelper.delete()
                        }
                    }, label: {
                        Text(userInfo.isAnonymous ? "Google 계정 연동" : "탈퇴")
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(.black, lineWidth: 1)
                            )
                    })
                }
                .frame(maxWidth: .infinity)
                .padding(5)
            }
        }
    }
}

