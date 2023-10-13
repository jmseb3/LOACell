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
                    }
                }
            }
            Spacer()
            Button {
                viewModel.commonViewModel.bottomAddAction()
            } label: {
                Image(systemName: "plus")
                    .foregroundColor(.black)
            }
            .frame(width: 40, height: 40)
            .background(ColorManager.BackgroundContainerColor)
            .shadow(radius: 5)
            .cornerRadius(10)
            
        }
        .padding()
        .frame(height: 80)
        .background(ColorManager.BackgroundColor)
    }
}


#Preview {
    BottomAppBar().environmentObject(LoaCellViewModel())
}
