//
//  RaidView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RaidView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    

    var body: some View {
        ZStack {
            ScrollView {
                LazyVStack(){
                    ForEach(viewModel.totalRoomInfo.raidInfoList,id: \.raidId) { raidInfo in
                        RaidItemRow(raidInfo: raidInfo) {
                            viewModel.setNowRaidInfo(raidId: raidInfo.raidId)
                        }
                    }
                }.padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10))
            }
            if viewModel.focusRaidId.isNotEmpty {
                RaidPartyView(characterList: viewModel.totalRoomInfo.partyCharacterList as! [shared.Character?]) { index in
                    
                } deleteAction: { index in
                    
                }

            }
        }
    }
}
