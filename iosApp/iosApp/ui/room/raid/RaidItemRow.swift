//
//  RaidItemRow.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RaidItemRow: View {
    let raidInfo : RaidInfo
    var onClick :() -> Void
    
    private let size : CGFloat = 100
    private let rShape  = RoundedRectangle(cornerRadius: 10)
    
    var body: some View {
        ZStack {
            HStack {
                let imgSrc = raidInfo.getImg()
                if imgSrc != nil {
                    Image(uiImage: imgSrc!.toUIImage()!)
                        .resizable()
                        .frame(width:size,height: size)
                        .clipShape(rShape)
                } else {
                    Spacer()
                        .frame(width: size)
                }
                VStack(alignment: .leading){
                    Text(raidInfo.title)
                        .font(.title3)
                    Text(raidInfo.getRaidText())
                    Text(raidInfo.makeGateText())
                    Spacer()
                }.padding(3)
                Spacer()
            }
            .background(rShape.fill(Color.gray).opacity(0.75))
            .onTapGesture {
                onClick()
            }
            .frame(maxWidth: .infinity)
            IconButton(resource: raidInfo.isFinish ? \.task_finish_done : \.task_finish_not) {
                CommonRaidHelper().updateFinish(
                    roomId: raidInfo.roomId,
                    raidId: raidInfo.raidId,
                    isFinish: !raidInfo.isFinish
                )
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottomTrailing)
            .padding(EdgeInsets(top: 0, leading: 0, bottom: 5, trailing: 5))
        }
        .frame(height: size)
    }
}
