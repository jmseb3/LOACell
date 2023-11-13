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
            LazyVStack {
                ForEach(Array(characterList.enumerated()), id: \.offset) { idx, item in
                    VStack {
                        if idx == 4 {
                            Divider()
                        }
                        HStack{
                            if item == nil {
                                Text("캐릭터를 추가해 주세요")
                                IconButton(resource: \.add) {
                                    openAction(idx)
                                }
                            } else {
                                VStack{
                                    Text(item!.name)
                                    HStack{
                                        Text(item!.className)
                                        Text(item!.level)
                                    }
                                }
                                IconButton(resource: \.delete_) {
                                    deleteAction(idx)
                                }
                            }
                            
                        }
                    }
                    
                }
            }
            .cornerRadius(20)
            .border(.black)
        }
    }
}
