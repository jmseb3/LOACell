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
    
    var totalRoomInfo : TotalRoomInfo {
        viewModel.totalRoomInfo
    }
    
    var tabState : RoomState {
        totalRoomInfo.tabState
    }
    var showSetting :Bool {
        viewModel.showSetting
    }

    
    var body: some View {
        HStack {
            HStack {
                if (viewModel.roomId.isEmpty) {
                    if (!showSetting) {
                        HStack {
                            IconButton(resource: \.refresh) {
                                viewModel.syncStart(force: false)
                            }
                        }
                    }
                }else {
                    if (viewModel.userInfo == nil && viewModel.raidInfo == nil) {
                        HStack {
                            IconButton(resource: \.room) {
                                viewModel.setTabStatus(state: RoomState.raid)
                            }
                            IconButton(resource: \.person) {
                                viewModel.setTabStatus(state: RoomState.user)
                            }
                            if (viewModel.myRole == RoomRole.owner || viewModel.myRole == RoomRole.manager) {
                                IconButton(resource: \.room_setting) {
                                    viewModel.setTabStatus(state: RoomState.setting)
                                }
                            }
                            
                        }
                    } else if(viewModel.userInfo != nil) {
                        HStack {
                            IconButton(resource: \.change_person) {
                                viewModel.showDialog(modal: shared.Dialog.characterEdit)
                            }
                            IconButton(resource: \.refresh, enabled: viewModel.userInfo!.checkTimeOver()) {
                                viewModel.updateCharacter(roomId: viewModel.roomId, userInfo: viewModel.userInfo!)
                            }.disabled(!viewModel.userInfo!.checkTimeOver())
                        }
                    } else if(viewModel.raidInfo != nil) {
                        HStack {
                            IconButton(resource: \.room_setting) {
                                viewModel.showDialog(modal: Sheet.raidEdit)
                            }
                            let info = viewModel.raidInfo!
                            IconButton(resource: info.isFinish ? \.task_finish_done : \.task_finish_not) {
                                CommonRaidHelper().updateFinish(
                                    roomId: info.roomId,
                                    raidId: info.raidId,
                                    isFinish: !info.isFinish
                                )
                            }
                        }
                    }
                }
            }
            Spacer()
            if (tabState == RoomState.setting || showSetting) {
                
            } else {
                Button {
                    viewModel.bottomAddAction()
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

        }
        .padding()
        .frame(height: height)
        .background(ColorManager.BackgroundColor)
    }
}
