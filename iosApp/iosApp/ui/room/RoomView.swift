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

    var body: some View {
        VStack {
            if (viewModel.focusRaidId.isEmpty && viewModel.focusUserName.isEmpty) {
                ZStack {
                    VStack(alignment: .leading) {
                        Text(viewModel.totalRoomInfo.roomInfo?.description_ ?? "")
                        Text(viewModel.totalRoomInfo.roomInfo?.uniqueId ?? "")
                        Divider()
                    }.padding()
                    
                    VStack() {
                        if(viewModel.myRole == RoomRole.owner || viewModel.myRole == RoomRole.none) {
                      
                        } else {
                            IconButton(resource: \.room_exit) {
                                
                            }
                        }
                    }
                    .frame(maxWidth: .infinity , alignment: .trailing)
                    .padding()
                    
                }
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
