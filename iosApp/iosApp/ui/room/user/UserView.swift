//
//  UserView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct UserView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    var body: some View {
        ZStack {
            ScrollView {
                LazyVStack {
                    ForEach(viewModel.totalRoomInfo.userInfoList , id: \.name) { item in
                        Button {
                            viewModel.setNowUserInfo(userName: item.name)
                        } label: {
                            Text("\(item.name) - \(item.representativeCharacter)")
                                .foregroundColor(.black)
                                .frame(maxWidth: .infinity)
                                .background(RoundedRectangle(cornerRadius: 50).fill(ColorManager.BackgroundContainerColor2))
                                .padding(EdgeInsets(top: 2, leading: 0, bottom: 2, trailing: 0))
                        }.padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))
                    }
                }
            }
            
            if(!viewModel.focusUserName.isEmpty) {
                UserFocusView()
            }
        }
    }
}
