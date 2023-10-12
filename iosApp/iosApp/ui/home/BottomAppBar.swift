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
                        Button {
                            viewModel.syncStart()
                        } label: {
                            Image(resource: \.refresh)
                                .foregroundColor(.black)
                        }
                        .frame(width: iconSize, height: iconSize)
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
