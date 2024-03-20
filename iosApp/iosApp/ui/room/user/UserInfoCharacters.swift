//
//  UserInfoCharacters.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
struct UserInfoCharacters: View {
    let userInfo : UserInfo
    let characterList : [Character]
    
    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
                    ForEach(characterList,id: \.name) {character in
                        VStack(alignment: .leading) {
                            DropDownCharacterNameView(name: character.name)
                                .fontWeight(userInfo.representativeCharacter == character.name ? .bold : .regular)
                            HStack() {
                                Text(character.className)
                                    .frame(minWidth: 0, maxWidth: .infinity,alignment: .leading)
                                Text(character.level)
                                    .frame(minWidth: 0, maxWidth: .infinity,alignment: .leading)
                            }
                            Divider()
                        }
                    }
                }
            }
            Spacer()
        }
        
    }
}

