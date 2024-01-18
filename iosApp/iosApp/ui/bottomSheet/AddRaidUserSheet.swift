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
    let userAndCharacterMap : [String : [shared.Character]]
    let successAction :(shared.Character) -> Void
    
    private let pickerHeight : CGFloat = 150
    
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
            errorMsg: .constant("")
        ) {
            VStack {
                HStack {
                    
                    Picker("Choose a User", selection: $selectedUser) {
                        ForEach(userList, id: \.self) { name in
                            Text(name)
                        }
                    }
                    .frame(height: pickerHeight)
                    .pickerStyle(.wheel)
                    .clipped()
                    if selectedUser.isNotEmpty {
                        if let characterList : [shared.Character] = userAndCharacterMap[selectedUser] {
                            Picker("Choose a Character", selection: $selectedCharacterIndex) {
                                ForEach(characterList, id:\.self) { chr in
                                    Text(chr.name)
                                }
                            }
                            .frame(height: pickerHeight)
                            .pickerStyle(.wheel)
                            .clipped()
                            .id(id)
                        }
                    }
                }
                Divider()
                Spacer()
                    .frame(height:10)
                if let ch = selectedCharacter {
                    HStack{
                        Spacer()
                        Text(ch.className)
                        Spacer()
                        Text(String(ch.getLevel()))
                        Spacer()
                    }
                }
                Text(selectedUser)
            }
        }.onAppear {
            selectedUser = userList[0]
        }
    }
}
