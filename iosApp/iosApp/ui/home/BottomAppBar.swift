//
//  BottomAppBar.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/12.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct BottomAppBar: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    let height :CGFloat
    let iconSize : CGFloat = 30
    
    var body: some View {
        HStack {
            HStack {
                if (viewModel.roomId.isEmpty && !viewModel.showSetting) {
                    HStack {
                        IconButton(resource: \.refresh) {
                            viewModel.syncStart()
                        }
                    }
                }else {
                    if (viewModel.userInfo == nil && viewModel.raidInfo == nil) {
                        HStack {
                            IconButton(resource: \.room) {
                                viewModel.commonViewModel.setTabStatus(state: RoomState.raid)
                            }
                            IconButton(resource: \.person) {
                                viewModel.commonViewModel.setTabStatus(state: RoomState.user)
                            }
                            if (viewModel.myRole == RoomRole.owner || viewModel.myRole == RoomRole.manager) {
                                IconButton(resource: \.room_setting) {
                                    viewModel.commonViewModel.setTabStatus(state: RoomState.setting)
                                }
                            }
                            
                        }
                    } else if(viewModel.userInfo != nil) {
                        HStack {
                            IconButton(resource: \.change_person) {
                                viewModel.commonViewModel.showDialog(status: DialogStatus.characterEdit)
                            }
                            IconButton(resource: \.refresh, enabled: viewModel.userInfo!.checkTimeOver()) {
                                viewModel.commonViewModel.updateCharacter(roomId: viewModel.roomId, userInfo: viewModel.userInfo!)
                            }.disabled(!viewModel.userInfo!.checkTimeOver())
                        }
                    }
                }
            }
            Spacer()
            Button {
                viewModel.commonViewModel.bottomAddAction()
            } label: {
                if !viewModel.focusUserName.isEmpty || !viewModel.focusRaidId.isEmpty {
                    Image(resource: \.delete_)
                        .resizable()
                        .foregroundColor(.black)
                        .frame(width: 18, height: 18)
                } else {
                    Image(systemName: "plus")
                        .foregroundColor(.black)
                }
                
            }
            .frame(width: 40, height: 40)
            .background(ColorManager.BackgroundContainerColor)
            .shadow(radius: 5)
            .cornerRadius(10)
            
        }
        .padding()
        .frame(height: height)
        .background(ColorManager.BackgroundColor)
    }
}
