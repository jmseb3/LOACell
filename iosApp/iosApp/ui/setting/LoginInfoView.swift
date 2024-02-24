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
    var myRoomList : [RoomInfo] {
        viewModel.roomList.filter { info in
            info.owner == user?.uid
        }
    }
    
    @Environment(\.window) var window: UIWindow?
    
    @State private var showDeleteError = false
    @State private var appleLinkCoordinator: AppleLinkCoordinator?
    @State private var appleRevokeCoordinator :AppleTokenRevokeCoordinator?
    @State private var isAppleProvider :Bool = false
    
    private let iconSize : CGFloat = 36
    var body: some View {
        VStack {
            if let userInfo = user {
                HStack {
                    VStack(alignment: .leading) {
                        Text((userInfo.displayName ?? "").ifEmpty{ "이름 없음"})
                            .fontWeight(.bold)
                        Text(userInfo.uid)
                    }
                    Spacer()
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
                                deleteAccount()
                            }
                        }
                    }
                    if showDeleteError {
                        Text("소유자인 방의 정보를 모두 삭제해 주세요")
                            .onAppear {
                                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1.5) {
                                    showDeleteError = false
                                }
                            }
                    }
                    if userInfo.isAnonymous {
                        HStack {
                            Text("연동하기")
                            Spacer()
                                .frame(width: 10)
                            Button {
                                linkToGoogle()
                            } label: {
                                Image(resource: \.btn_google)
                                    .resizable()
                                    .frame(width: iconSize, height: iconSize)
                                
                            }
                            Button {
                                linkToApple()
                            } label: {
                                Image(resource: \.logo_apple)
                                    .resizable()
                                    .frame(width: iconSize, height: iconSize)
                                
                            }
                            Spacer()
                        }
                      

                    }
                }
                .frame(maxWidth: .infinity)
                .padding(5)
            }
        }
        .onAppear {
            Auth.auth().currentUser?.providerData.forEach({ info in
                print(info.providerID)
                if (info.providerID == "apple.com") {
                    isAppleProvider = true
                }
            })
        }
    }
    
    private func linkToGoogle() {
        loginHelper.requestAnonymousToGoogleLogin(
            failAction: { msg in
                viewModel.showSnackBar(msg: msg)
            },successAction: {
                
            }
        )
    }
    
    private func linkToApple() {
        appleLinkCoordinator = AppleLinkCoordinator(
            window: window,
            linkFailAction: { msg in
                viewModel.showSnackBar(msg: msg)
            },
            linkSuccessAction: {
                viewModel.closeSetting()
            })
        appleLinkCoordinator?.startLogin()
    }
    
    private func deleteAccount() {
        if !myRoomList.isEmpty {
            showDeleteError = true
            return
        }
        if !isAppleProvider {
            loginHelper.delete()
            return
        }
        appleRevokeCoordinator = AppleTokenRevokeCoordinator(window: window, failAction: { msg in
            viewModel.showSnackBar(msg: msg)
        }, revokeSuccessAction: {
            print("revoke Success")
            loginHelper.delete()
        })
        appleRevokeCoordinator?.startLogin()
    }
}

