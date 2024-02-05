//
//  SettingView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine
import FirebaseAuth

struct SettingView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    @State private var showSlider :Bool = false
    @Environment(\.openURL) private var openURL
    
    private var defaultValue : Binding<CGFloat> {
        Binding {
            return viewModel.defaultSpace
        } set: { value in
            viewModel.setSheetSpace(space: value)
        }
    }
    
    private var user : FBUser?  {
        viewModel.user
    }
    private var loginHelper : LoginHelper {
        self.viewModel.loginHelper
    }
    private var myRoomList : [RoomInfo] {
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
        List {
            if let userInfo = user {
                Section {
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
                    Button(action: {
                        viewModel.outOrSignOut()
                    }, label: {
                        Label(
                            title: { Text(userInfo.isAnonymous ? "나가기" : "로그아웃") },
                            icon: { Image(systemName: "rectangle.portrait.and.arrow.right") }
                        )
                        .foregroundColor(.blue)
                    })
                    if !userInfo.isAnonymous {
                        Button(action: {
                            deleteAccount()
                        }, label: {
                            Label(
                                title: { Text("탈퇴") },
                                icon: { Image(systemName: "power.circle") }
                            )
                            .foregroundColor(.red)
                        })
                        if showDeleteError {
                            Text("소유자인 방의 정보를 모두 삭제해 주세요")
                                .onAppear {
                                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1.5) {
                                        showDeleteError = false
                                    }
                                }
                        }
                    }
                } header: {
                    Text("로그인 정보")
                }
                
                if userInfo.isAnonymous {
                    Section {
                        Button {
                            linkToApple()
                        } label: {
                            Label(
                                title: { Text("Link to Apple") },
                                icon: {
                                    Image(resource: \.logo_apple)
                                        .resizable()
                                    .frame(width: iconSize, height: iconSize) }
                            )
                        }
                        Button {
                            linkToGoogle()
                        } label: {
                            Label(
                                title: { Text("Link to Google") },
                                icon: {
                                    Image(resource: \.btn_google)
                                        .resizable()
                                        .frame(width: iconSize, height: iconSize)
                                }
                            )
                        }
                    }header: {
                        Text("연동하기")
                    }
                }
            }
            
            Section {
                Button("시트 하단 여백 크기 조정") {
                    showSlider = !showSlider
                }
                .foregroundColor(.black)
                if showSlider {
                    Slider(value: defaultValue, in: 0...40, step: 1)
                    HStack() {
                        Text("하단 여백 크기 : \(Int(defaultValue.wrappedValue))")
                        Spacer()
                        RoundCornerButton(text: "테스트") {
                            viewModel.showDialog(dialogStatus: DialogStatus.testSheet)
                        }
                    }
                }
            } header: {
                Text("조정")
            }
            Section {
                Button("버그 제보 및 건의하기") {
                    if let link = URL(string: "https://discord.gg/acD6rQ9Tja") {
                        openURL(link)
                    }
                }
                .foregroundColor(.black)
                HStack {
                    Text("앱 버전")
                    Spacer()
                    Text("\(Bundle.main.releaseVersionNumber!)(\(Bundle.main.buildVersionNumber!))")
                }
            } header: {
                Text("About")
            }
        }
                .modifier(FormHiddenBackground())
        .onAppear {
            Auth.auth().currentUser?.providerData.forEach({ info in
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
