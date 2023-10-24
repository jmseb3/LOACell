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
    let characterList :[shared.Character?]
    var openAction : (_ index:Int) -> Void
    var deleteAction : (_ index:Int) -> Void
    
    private let height : CGFloat = 50
    
    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
//                    ForEach(Array(zip(characterList.indices, characterList)), id: \.0) { index, item in
//                        VStack {
//                            if index == 4 {
//                                Divider()
//                            }
//                            
//                            if item == nil {
//                                HStack{
//                                    Text("캐릭터를 추가해 주세요")
//                                    IconButton(resource: \.add) {
//                                        openAction(index)
//                                    }
//                                }
//                            } else {
//                                HStack{
//                                    VStack{
//                                        Text(item!.name)
//                                        HStack{
//                                            Text(item!.className)
//                                            Text(item!.level)
//                                        }
//                                    }
//                                    IconButton(resource: \.delete) {
//                                        deleteAction(index)
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
                .cornerRadius(20)
                .border(.black)
            }
        }
    }
}
