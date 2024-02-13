//
//  RoomView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RoomView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var showTitle :Bool {
        withAnimation {
            return viewModel.focusRaidId.isEmpty && viewModel.focusUserName.isEmpty && viewModel.tabState != RoomState.setting
        }
    }
    var body: some View {
        VStack {
            if (showTitle) {
                if let room = viewModel.roomInfo {
                    RoomTitleView(roomInfo: room, role: viewModel.myRole) { status in
                        viewModel.showDialog(dialogStatus: status)
                    }.onAppear{
                        print("role \(viewModel.myRole)")
                    }
                }
            }
            if viewModel.tabState == RoomState.raid {
                RaidView()
            } else if(viewModel.tabState == RoomState.user) {
                UserView()
            } else if (viewModel.tabState == RoomState.setting) {
                SettingRoomView()
            }
        }
    }
}

struct RoomTitleView : View {
    let roomInfo : RoomInfo
    let role : RoomRole
    let showDialog : (_ status : DialogStatus) -> Void
    var body: some View {
        ZStack {
            VStack(alignment: .leading) {
                Text(roomInfo.description_)
                Text(roomInfo.uniqueId)
                    .onTapGesture {
                        showDialog(DialogStatus.shareSheet)
                    }
                Divider()
            }.padding(EdgeInsets(top: 3, leading: 3, bottom: 3, trailing: 3))
            
            VStack() {
                if(role == RoomRole.owner) {
                    
                } else {
                    IconButton(resource: \.room_exit) {
                        showDialog(DialogStatus.roomExit)
                    }
                }
            }
            .frame(maxWidth: .infinity , alignment: .trailing)
            .padding()
            
        }
    }
}
