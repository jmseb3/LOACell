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
