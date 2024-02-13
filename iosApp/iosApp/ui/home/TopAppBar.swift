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
                    withAnimation {
                        viewModel.topBackAction()
                    }
                } label: {
                    Image(systemName: "arrow.left")
                }
                .foregroundColor(.black)
                .transition(.slide)
            }
            HStack {
                if(viewModel.showSetting) {
                    Text("설정")
                } else if(!viewModel.focusUserName.isEmpty) {
                    Text("\(viewModel.focusUserName)님 캐릭터 정보")
                } else {
                    Text(viewModel.raidInfo?.title ?? viewModel.roomInfo?.title ?? "LoaCell")
                }
            }.transition(.slide)
            Spacer()
            
            if viewModel.roomId.isEmpty {
                Button {
                    viewModel.showSetting = true
                } label: {
                    Image(systemName: "gearshape.fill")
                }
                .foregroundColor(viewModel.showSetting ? .gray : .black)
                .disabled(viewModel.showSetting)
            }
        }.padding()
    }
}
