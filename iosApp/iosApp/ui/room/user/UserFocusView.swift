//
//  UserFocusView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import CustomAlert

struct UserFocusView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    @State var now = ""
    var body: some View {
        let binding = Binding<Bool>(get: {viewModel.dialogStatus == DialogStatus.characterEdit}, set: {_ in viewModel.commonViewModel.hideAllDialog()})
        ZStack {
            if(viewModel.userInfo != nil) {
                UserInfoCharacters(userInfo: viewModel.userInfo!, characterList: viewModel.characterList)
                if(viewModel.showLoading) {
                    LoadingView(info: viewModel.msg)
                }
            }
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .background(Color.white)
        .customAlert("대표 캐릭터 변경", isPresented: binding) {
            Picker("대표 캐릭터 변경",selection: $now) {
                ForEach(viewModel.characterList ,id: \.name) {item in
                    Text(item.name)
                        .foregroundColor(.black)
                        .fontWeight(viewModel.userInfo?.representativeCharacter == item.name ? .bold : .regular)
                        .tag(item.name)
                }
            }.onAppear{
                now = viewModel.userInfo!.representativeCharacter
            }
        } actions: {
            MultiButton {
                Button("취소",role:.cancel) {}
                Button("변경",role:nil) {
                    CommonUserHelper().updateRepresentativeCharacter(roomId: viewModel.roomId, name: viewModel.userInfo!.name, representativeCharacter: now)
                    viewModel.commonViewModel.hideAllDialog()
                }.disabled(now == viewModel.userInfo!.representativeCharacter)
            }
        }
    }
}
