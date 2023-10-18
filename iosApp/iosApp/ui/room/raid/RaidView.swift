//
//  RaidView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct RaidView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var body: some View {
        ScrollView {
            LazyVStack(){
                ForEach(viewModel.totalRoomInfo.raidInfoList,id: \.raidId) { raidInfo in
                    RaidItemRow(raidInfo: raidInfo) {
                        viewModel.commonViewModel.setNowRaidInfo(raidId: raidInfo.raidId)
                    }
                }
            }.padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10))
        }
    }
}
