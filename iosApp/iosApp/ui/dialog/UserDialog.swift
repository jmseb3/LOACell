//
//  UserDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct EditCharacterDialog: View {
    
    let characterList : [Character]
    let representativeCharacter : String
    @State var now :String = ""
    var leftAction : () -> Void
    var rightAction :(_ name:String) -> Void
    
    var body: some View {
        BaseDialog(
            title: "대표 캐릭터 변경",
            rightText: "변경",
            leftAction: {
                self.leftAction()
            }, 
            rightAction: {
                self.rightAction(now)
            },
            rightEnabled: Binding {
                now != representativeCharacter
            } set: { _ in
                
            }
        ) {
            VStack {
                Picker("대표 캐릭터 변경",selection: $now) {
                    ForEach(characterList ,id: \.name) {item in
                        Text(item.name)
                            .foregroundColor(.black)
                            .fontWeight(representativeCharacter == item.name ? .bold : .regular)
                            .tag(item.name)
                    }
                }
                .accentColor(.black)
                .onAppear{
                    now = representativeCharacter
                }
            }
        }
    }
    
}
