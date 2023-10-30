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
    @State private var now :String = ""

    let userInfo : shared.UserInfo
    let characterList : [Character]
    var dismiss : () -> Void
    var success :(_ name:String) -> Void
    
    private var representativeCharacter : String {
        userInfo.representativeCharacter
    }
    
    var body: some View {
        BaseDialog(
            title: "대표 캐릭터 변경",
            rightText: "변경",
            leftAction: {
                self.dismiss()
            },
            rightAction: {
                self.success(now)
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

struct DeleteCharacterDialog :  View {
    
    let name: String
    let dismiss: () -> Void
    let confirm: () -> Void
    
    var body: some View {
        DeleteDialog(
            title: "유저 정보 삭제",
            leftAction: dismiss,
            rightAction: confirm,
            rightEnabled: .constant(true)
        ) {
            Text("\(name)님 의 정보를 삭제 하시겠습니까?")
                .font(.system(size: 13))
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
