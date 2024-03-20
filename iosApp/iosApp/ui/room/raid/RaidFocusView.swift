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
    
    var raidView: some View {
        VStack {
            if raidInfo != nil {
                VStack(alignment: .leading) {
                    Text("\(raidInfo!.getRaidText()) \(raidInfo!.makeGateText())")
                        .frame(maxWidth: .infinity, alignment: .leading)
                    if (raidInfo!.day != Day.none) {
                        Text(raidInfo!.getDayText())
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                RaidPartyView(
                    characterList: totalRoomInfo.partyCharacterList as! [Character?]
                ) { index in
                    let userAndCharacterMap = totalRoomInfo.userAndCharacterMap
                    if userAndCharacterMap.isEmpty {
                        viewModel.showSnackBar(
                            msg: "추가 가능한 인원이 없습니다.",
                            label: "이동"
                        ) {
                            viewModel.clearFocusItem()
                            viewModel.setTabStatus(state: RoomState.user)
                            viewModel.showDialog(dialogStatus: DialogStatus.userAdd)
                        }
                    } else {
                        viewModel.updatePartyFocusIndex(index: index)
                        viewModel.showDialog(dialogStatus: DialogStatus.raidUserAdd)
                    }
                    
                } deleteAction: { index in
                    viewModel.updatePartyFocusIndex(index: index)
                    viewModel.showDialog(dialogStatus: DialogStatus.raidUserDelete)
                }
            }
        }
        .padding()
        .frame(minWidth: 400)
        
    }
    
    @State private var image : UIImage? = nil
    
    var body: some View {
        VStack {
            raidView
            ShareLink(
                item: Image(uiImage: render()),
                preview: SharePreview("공격대 정보", image: Image(uiImage: render()))
            ) {
                Label(
                    title: {
                        Text("공격대 공유")
                            .foregroundColor(.white)
                            .font(.footnote)
                    },
                    icon: {
                        Image(resource: \.screenshot)
                            .renderingMode(.template)
                            .foregroundColor(.white)
                    }
                )
            }
            .padding(EdgeInsets(top: 5, leading: 8, bottom: 5, trailing: 8))
            .background(.blue)
            .cornerRadius(15)
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .background(Color.white)
    }
    
    @MainActor func render()  -> UIImage {
        return raidView.snapshot()
    }
}
