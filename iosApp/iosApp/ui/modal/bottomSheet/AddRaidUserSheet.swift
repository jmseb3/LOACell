//
//  AddRaidUserSheer.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/14.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AddRaidUserSheet: View {
    private let userAndCharacterMap : [String : [shared.Character]]
    private let successAction :(shared.Character) -> Void
    private let dismiss : () -> Void
    
    init(dialogAction : DialogAction) {
        self.userAndCharacterMap = dialogAction.getUserAndCharacterMap()
        self.successAction = {character in
            dialogAction.dialogUserAdd(character : character)
        }
        self.dismiss = {
            dialogAction.hideDialog()
        }
    }
    
    private let pickerHeight : CGFloat = 40
    
    private var userList : [String] {
        Array(userAndCharacterMap.keys)
    }
    
    @State private var selectedUser : String = "" {
        willSet {
            selectedCharacterIndex = 0
            id = UUID()
        }
    }
    @State private var selectedCharacterIndex : Int = 0 {
        willSet{
            print("ch idnex ch")
        }
    }
    @State private var id: UUID = UUID()
    
    private var selectedCharacter : shared.Character? {
        if (selectedUser == "") {
            return nil
        }
        if let cl =  userAndCharacterMap[selectedUser] {
            return cl[selectedCharacterIndex]
        } else {
            return nil
        }
    }
    var body: some View {
        BaseSheet(
            title: "캐릭터 정보 추가",
            text: "추가",
            action: {
                guard let character = selectedCharacter else {
                    return
                }
                successAction(character)
            },
            enabled: selectedCharacter != nil,
            errorMsg: .constant(""),
            dismiss: dismiss
        ) {
            Form {
                Section(header:Text("유저 및 캐릭터 선택")) {
                    Picker("유저 이름", selection: $selectedUser) {
                        ForEach(userList, id: \.self) { name in
                            Text(name)
                        }
                    }
                    if selectedUser.isNotEmpty {
                        if let characterList : [shared.Character] = userAndCharacterMap[selectedUser] {
                            Picker("캐릭터 명", selection: $selectedCharacterIndex) {
                                ForEach(0 ..< characterList.count) { chr in
                                    Text(characterList[chr].name)
                                }
                            }
                            .id(id)
                        }
                    }
                }
                Section(header:Text("선택 정보")) {
                    if let ch = selectedCharacter {
                        HStack{
                            Text(ch.className)
                            Spacer()
                            Text(String(ch.getLevel()))
                        }
                    }
                }
            }
            .frame(height: 250)
            .scrollContentBackground(.hidden)
        }.onAppear {
            selectedUser = userList[0]
        }
    }
}
