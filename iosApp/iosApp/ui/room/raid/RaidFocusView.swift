//
//  RaidFocusView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine

struct RaidFocusView: View {
    @Environment(\.displayScale) var displayScale
    
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var totalRoomInfo : TotalRoomInfo {
        viewModel.totalRoomInfo
    }
    
    var raidInfo : RaidInfo? {
        totalRoomInfo.raidInfo
    }
    
    @State private var tabIndex : Int = 0
    private let partyIndex = [0,4,8,12]
    
    var body: some View {
        VStack {
            GeometryReader { geo in
                VStack{
                    Tabs(tabs: totalRoomInfo.getTabList(), geoWidth: geo.size.width, selectedTab: $tabIndex)
                    if(tabIndex == 0) {
                        if let info = raidInfo {
                            RaidPartySimpleView(
                                raidInfo: info,
                                characterList: totalRoomInfo.partyCharacterList as! [Character?]
                            )
                        } else {
                            EmptyView()
                        }
                  
                    } else {
                        let stIdx = partyIndex[tabIndex - 1]
                        let partyList = totalRoomInfo.getSubPartyList(stIdx: Int32(stIdx)) as? [Character?]
                        if (partyList == nil) {
                            EmptyView()
                        } else {
                            RaidPartyView(
                                characterList: partyList!
                            ) { index in
                                let newIndex = stIdx + index
                                let userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                                if userAndCharacterMap.isEmpty {
                                    viewModel.showSnackBar(
                                        msg: "추가 가능한 인원이 없습니다.",
                                        label: "이동"
                                    ) {
                                        viewModel.clearFocusItem()
                                        viewModel.setTabStatus(state: RoomState.user)
                                        viewModel.showDialog(modal: Sheet.userAdd)
                                    }
                                } else {
                                    viewModel.updatePartyFocusIndex(index: Int32(newIndex))
                                    viewModel.showDialog(modal: Sheet.raidUserAdd)
                                }
                                
                            } deleteAction: { index in
                                let newIndex = stIdx + index
                                viewModel.updatePartyFocusIndex(index: Int32(newIndex))
                                viewModel.showDialog(modal: Dialog.raidUserDelete)
                            }
                        }
                    }
                }
            }
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .background(Color.white)
    }

}
