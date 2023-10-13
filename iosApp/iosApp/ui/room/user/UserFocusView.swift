//
//  UserFocusView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct UserFocusView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel

    var body: some View {
        VStack {
            if(viewModel.userInfo != nil) {
                UserInfoCharacters(userInfo: viewModel.userInfo!, characterList: viewModel.characterList)
            }
        }.frame(maxWidth: .infinity,maxHeight: .infinity)
            .background(Color.white)
    }
}
