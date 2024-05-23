//
//  MainContent.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/07.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainContent: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    @State var text :String = "empty"
    var body: some View {
        ZStack {
            VStack {
                if viewModel.showSetting {
                    //설정화면
                    SettingView()
                } else {
                    if viewModel.roomId.isEmpty {
                        //방리스트
                        RoomListView(roomList: viewModel.roomList) { roomId in
                            viewModel.showRoomInfo(roomId: roomId)
                        }
                    } else {
                        // 방정보
                        RoomView()
                    }
                }
                Spacer()
                
            }
            VStack {
                if(viewModel.syncData) {
                    LoadingView(info: "데이터를 동기화 중입니다.")
                }
            }
        }
        .onAppear{
            CommonFireStorageHelper().parseSynergyJson()
        }
    }
    
}
