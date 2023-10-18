//
//  AddUserSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AddUserSheet: View {
    let roomId :String
    @State private var searchCharacterName : String = "test" {
        didSet {
            if searchCharacterName.count > 12 && oldValue.count <= 12 {
                searchCharacterName = oldValue
            }
        }
    }
    @State private var user :String = "test"{
        didSet {
            if searchCharacterName.count > 5 && oldValue.count <= 5 {
                searchCharacterName = oldValue
            }
        }
    }
    @State private var searchResult : [CharacterInfo] = []
    @State private var showLoading :Bool = false
    @State private var errorMsg :String = ""
    var dismiss : () -> Void
    
    @MainActor
    private func searchAction() {
        showLoading = true
        Task {
            do {
                let characterResult = try await LostArkApi().getCharacterInfo(characterName:searchCharacterName)
                if characterResult is LostArkResultSuccess {
                    searchResult = (characterResult as! LostArkResultSuccess).data as! [CharacterInfo]
                } else if characterResult is LostArkResultFail {
                    let fail = (characterResult as! LostArkResultFail)
                    errorMsg = "\(fail.message)(\(fail.code))"
                } else if characterResult is LostArkResultFailOnlyMsg {
                    errorMsg = (characterResult as! LostArkResultFailOnlyMsg).message
                }
            } catch {
                errorMsg = error.localizedDescription
            }
            showLoading = false
        }
    }
    
    private func initAction() {
        if !searchResult.isEmpty {
            CommonUserHelper().addOrUpdate(
                roomId: roomId,
                name: user,
                representativeCharacter: searchCharacterName,
                characterList: searchResult
            ) { error in
                errorMsg = error
            } successAction: {
                dismiss()
            }
            
        }
    }
    
    var body: some View {
        BaseSheet(
            title : "유저 정보 추가",
            text: searchResult.isEmpty ? "검색" : "추가",
            action: {
                if (searchResult.isEmpty) {
                    searchAction()
                } else {
                    initAction()
                }
            },
            enabled: Binding(
                get: {
                    (searchResult.isEmpty && !user.isEmpty && !searchCharacterName.isEmpty) || !searchResult.isEmpty
                }, set: { _ in
                    
                }
            ),
            errorMsg: $errorMsg
        )
        {
            VStack {
                LengthLimitTextField(maxLength: 5, placeHolder: "유저 이름 입력", text: $user)
                LengthLimitTextField(maxLength: 12, placeHolder: "대표 캐릭터 입력", text: $searchCharacterName)
                if(showLoading) {
                    HStack{
                        Text("\(user)님의 캐릭터 정보를 불러옵니다.")
                        ProgressView()
                    }
                }
                if !searchResult.isEmpty {
                    if let find = searchResult.first(where: {$0.characterName.lowercased() == searchCharacterName.lowercased()}) {
                        let count = searchResult.count - 1
                        VStack {
                            Text("\(find.characterName)(\(find.characterClassName)) - \(find.itemMaxLevel)")
                            Text("외 \(count)개의 캐릭터를 찾았습니다.")
                        }
                    } else {
                        
                    }
                }
            }
        }
    }
}
