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

    let dialogAction :DialogAction

    private let userInfo : shared.UserInfo
    private let characterList : [Character]
    
    init(dialogAction: DialogAction) {
        self.dialogAction = dialogAction
        self.userInfo = dialogAction.getUserInfo()
        self.characterList = dialogAction.getCharacterList()
        self.now = userInfo.representativeCharacter
    }
    
    private var representativeCharacter : String {
        userInfo.representativeCharacter
    }
    
    var body: some View {
        BaseDialog(
            title: Dialog.characterEdit.title,
            rightText: "변경",
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                dialogAction.dialogEditName(name: now)
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
            }
        }
    }
    
}

struct DeleteCharacterDialog :  View {
    
    private let name: String
    let dialogAction :DialogAction

    init(dialogAction: DialogAction) {
        self.dialogAction = dialogAction
        self.name = dialogAction.getUserInfo().name
    }
    var body: some View {
        DeleteDialog(
            title: Dialog.characterDelete.title,
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                dialogAction.dialogCharacterDelete()
            },
            rightEnabled: .constant(true)
        ) {
            Text("\(name)님 의 정보를 삭제 하시겠습니까?")
                .font(.system(size: 13))
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
