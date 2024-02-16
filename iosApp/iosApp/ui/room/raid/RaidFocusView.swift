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
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    @State var anyCancellable = Set<AnyCancellable>()

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
                }.frame(maxWidth: .infinity)
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

    }

    @State private var image : UIImage? = nil

    var body: some View {
        VStack {
            raidView
        }
        .frame(maxWidth: .infinity,maxHeight: .infinity)
        .background(Color.white)
        .onAppear {
            viewModel.screenshotStart.sink { completion in
                print("Completion: \(completion)")
            } receiveValue: { value in
                if (value) {
                    image = raidView.snapshot()
                }
            }.store(in: &anyCancellable)
        }
        .imageShareSheet(isPresented: Binding, image: UIImage(named: "example_image"))
    }

}
