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
                    Spacer()
                } else {
                    if viewModel.roomId.isEmpty {
                        //방리스트
                        VStack {
                            RoomListView(roomList: viewModel.roomList) { roomId in
                                viewModel.commonViewModel.showRoom(roomId: roomId)
                            }
                            Spacer()
                        }
                    } else {
                        // 방정보
                        VStack {
                            Text(viewModel.roomId)
                            Spacer()
                        }
                    }
                }
            }
            VStack {
                if(viewModel.syncData) {
                    LoadingView(info: "데이터를 동기화 중입니다.")
                }
            }
        }
       
    }

}

#Preview {
    MainContent().environmentObject(LoaCellViewModel())
}

