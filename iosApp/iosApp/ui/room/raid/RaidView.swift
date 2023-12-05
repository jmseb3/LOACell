//
//  RaidView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RaidView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var totalRoomInfo : TotalRoomInfo {
        viewModel.totalRoomInfo
    }
    
    @State private var showType :RoomType = RoomType.default_
    
    var body: some View {
        ZStack {
            VStack {
                HStack {
                    IconButton(resource: \.calendar) {
                        if showType == RoomType.default_ {
                            showType = RoomType.calendar
                        } else {
                            showType = RoomType.default_
                        }
                    }
                    Spacer()
                    Button(
                        action: {
                            viewModel.showDialog(dialogStatus: DialogStatus.raidFilter)
                        },
                        label: {
                            HStack {
                                Image(resource: \.filter)
                                    .resizable()
                                    .frame(width: 18, height: 18)
                                
                                Text("Filter")
                                    .foregroundColor(.black)
                            }
                            .padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(.black, lineWidth: 1)
                            )
                        })
                }
                .padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10))
                Divider()
                ScrollView {
                    LazyVStack(){
                        ForEach(totalRoomInfo.filterList,id: \.raidId) { raidInfo in
                            RaidItemRow(raidInfo: raidInfo) {
                                viewModel.setNowRaidInfo(raidId: raidInfo.raidId)
                            }
                        }
                    }.padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10))
                }
            }
            .frame(maxWidth: .infinity,maxHeight: .infinity)
            
            if viewModel.focusRaidId.isNotEmpty {
                RaidFocusView()
            }
        }
    }
}
