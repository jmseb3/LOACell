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
        LazyVStack {
            ForEach(characterList,id: \.name) {character in
                VStack {
                    Text(character.name)
                        .fontWeight(userInfo.representativeCharacter == character.name ? .bold : .regular)
                    HStack {
                        Text(character.className)
                        Text(character.level)
                    }
                }
            }
        }.frame(maxWidth: .infinity,maxHeight: .infinity)
    }
}

