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
            return viewModel.focusRaidId.isEmpty && viewModel.focusUserName.isEmpty
        }
    }
    var body: some View {
        VStack {
            if (showTitle) {
                ZStack {
                    VStack(alignment: .leading) {
                        Text(viewModel.totalRoomInfo.roomInfo?.description_ ?? "")
                        Text(viewModel.totalRoomInfo.roomInfo?.uniqueId ?? "")
                        Divider()
                    }.padding(EdgeInsets(top: 3, leading: 3, bottom: 3, trailing: 3))
                    
                    VStack() {
                        if(viewModel.myRole == RoomRole.owner || viewModel.myRole == RoomRole.none) {
                      
                        } else {
                            IconButton(resource: \.room_exit) {
                                
                            }
                        }
                    }
                    .frame(maxWidth: .infinity , alignment: .trailing)
                    .padding()
                    
                }.animation(.easeInOut)
            }
            if(viewModel.tabState == RoomState.user) {
                UserView()
            }
        }
    }
}

#Preview {
    RoomView().environmentObject(LoaCellViewModel())
}
