//
//  UserView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct UserView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var body: some View {
        ZStack {
            LazyVStack {
                ForEach(viewModel.totalRoomInfo.userInfoList , id: \.name) { item in
                    VStack {
                        Button {
                            viewModel.commonViewModel.setNowUserInfo(userName: item.name)
                        } label: {
                            Text("\(item.name) - \(item.representativeCharacter)")
                        }.foregroundColor(.black)
                    }
                }
            }
            
            if(!viewModel.focusUserName.isEmpty) {
                UserFocusView()
            }
        }
    }
}
