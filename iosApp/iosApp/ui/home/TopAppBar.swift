//
//  TopAppBar.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/12.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct TopAppBar: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var body: some View {
        HStack {
            if (!viewModel.roomId.isEmpty || viewModel.showSetting) {
                Button {
                    viewModel.commonViewModel.topBackAction()
                } label: {
                    Image(systemName: "arrow.left")
                }
                .foregroundColor(.black)
                .animation(.easeIn)
            }
            if(viewModel.showSetting) {
                Text("설정")
            } else if(!viewModel.focusUserName.isEmpty) {
                Text("\(viewModel.focusUserName)님 캐릭터 정보")
            } else {
                Text(viewModel.raidInfo?.title ?? viewModel.totalRoomInfo.roomInfo?.title ?? "LoaCell")
            }
            
            Spacer()
            
            Button {
                viewModel.showSetting = true
            } label: {
                Image(systemName: "gearshape.fill")
            }
            .foregroundColor(viewModel.showSetting ? .gray : .black)
            .disabled(viewModel.showSetting)
            .onTapGesture {
                
            }
        }.padding()
    }
}

#Preview {
    TopAppBar().environmentObject(LoaCellViewModel())
}
