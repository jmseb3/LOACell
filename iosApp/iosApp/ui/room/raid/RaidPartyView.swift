//
//  RaidPartyView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/19.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import SwiftUI

struct RaidPartyView: View {
    let characterList : [shared.Character?]
    var openAction : (_ index:Int) -> Void
    var deleteAction : (_ index:Int) -> Void
    
    private let height : CGFloat = 50
    
    var body: some View {
        VStack {
            VStack {
                ForEach(Array(characterList.enumerated()), id: \.offset) { idx, item in
                    VStack {
                        if idx > 0 && idx % 4 == 0 {
                            Divider()
                        }
                        HStack{
                            VStack{
                                if item != nil {
                                    DropDownCharacterNameView(name: item!.name)
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                    HStack{
                                        Text(item!.className)
                                        Spacer()
                                        Text(item!.level)
                                    }
                                } else {
                                    Text("캐릭터를 추가해 주세요")
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                }
                            }
                            .padding(EdgeInsets(top: 0, leading: 5, bottom: 0, trailing: 5))
                            Spacer()
                            IconButton(resource: item == nil ? \.add : \.delete_) {
                                buttonAction(item: item, index: idx)
                            }
                        }
                        .padding(10)
                        .frame(maxWidth: .infinity)
                    }
                    .frame(maxWidth: .infinity)
                }
                
            }
            .cornerRadius(10) /// make the background rounded
            .overlay( /// apply a rounded border
                RoundedRectangle(cornerRadius: 10)
                    .stroke(.black, lineWidth: 1)
            )
            VStack {
                Text("시너지")
                ForEach(Synergy().getSynergyList(characterList: characterList),id:\.self) { data in
                    Text(data)
                }
            }
        }
        .padding(2)
        Spacer()
        
    }
    
    
    private func buttonAction(item: Character?,index:Int) {
        if (item == nil) {
            openAction(index)
        } else {
            deleteAction(index)
        }
    }
}

private struct RaidPartySimpleRow : View {
    let partyIndex :Int
    let characterList : [Character?]
    
    init(partyIndex: Int, characterList: [Character?]) {
        self.partyIndex = partyIndex
        self.characterList = characterList
    }


    var body: some View {
        HStack {
            Text("\(partyIndex / 4 + 1)")
                .fontWeight(.bold)
                .frame(width: 50,alignment: .center)
            
            ForEach(characterList[partyIndex..<partyIndex+4],id:\.self) { item in
                VStack {
                    if item != nil {
                        DropDownCharacterNameView(name: item!.name)
                        Text(item!.className)
                            .lineLimit(1)
                            .font(.system(size: 11))
                        Text(item!.level)
                            .lineLimit(1)
                            .font(.system(size: 11))
                    } else {
                        Text("X")
                    }
                }
            }
            .frame(maxWidth: .infinity)
        }
    }
}

struct RaidPartySimpleView: View {
    let raidInfo : RaidInfo
    let characterList : [Character?]
    
    var columns: [GridItem] = Array(repeating: .init(.flexible()), count: 5)

    var simpleView : some View {
        VStack {
            VStack(alignment: .leading) {
                Text("\(raidInfo.getRaidText()) \(raidInfo.makeGateText())")
                    .frame(maxWidth: .infinity, alignment: .leading)
                if (raidInfo.day != Day.none) {
                    Text(raidInfo.getDayText())
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
            }
            VStack {
                ForEach([4,8,12,16],id: \.self) { count in
                    if count > 4 {
                        Divider()
                    }
                    if(characterList.count >= Int(count)) {
                        RaidPartySimpleRow(partyIndex: count - 4, characterList: characterList)
                            .padding()
                    }
                }
            }
            .cornerRadius(10) /// make the background rounded
            .overlay( /// apply a rounded border
                RoundedRectangle(cornerRadius: 10)
                    .stroke(.black, lineWidth: 1)
            )
        }
        .frame(minWidth: 300)
        .padding(2)
    }
    
    var body: some View {
        VStack {
            simpleView
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
    }
    
    @MainActor func render()  -> UIImage {
        return simpleView.snapshot()
    }
}


