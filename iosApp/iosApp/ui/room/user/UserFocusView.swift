//
//  UserFocusView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct UserFocusView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    var totalRoomInfo : TotalRoomInfo {
        viewModel.totalRoomInfo
    }
    var userInfo : UserInfo? {
        totalRoomInfo.userInfo
    }
    var characterList : [Character] {
        totalRoomInfo.characterList
    }
    var body: some View {
        ZStack {
            if(userInfo != nil) {
                UserInfoCharacters(userInfo: userInfo!, characterList: characterList)
                if(viewModel.showLoading) {
                    LoadingView(info: viewModel.msg)
                }

            }
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .background(Color.white)
    }
}
