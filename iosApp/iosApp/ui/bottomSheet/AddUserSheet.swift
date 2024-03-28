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
    let dialogAction : DialogAction
    
    private var roomId :String {
        dialogAction.getRoomInfoUniqueId()
    }
    @State private var searchCharacterName : String = "" {
        didSet {
            if searchCharacterName.count > 12 && oldValue.count <= 12 {
                searchCharacterName = oldValue
            }
        }
    }
    @State private var user :String = ""{
        didSet {
            if searchCharacterName.count > 5 && oldValue.count <= 5 {
                searchCharacterName = oldValue
            }
        }
    }
    @State private var searchResult : [CharacterInfo] = []
    @State private var showLoading :Bool = false
    @State private var errorMsg :String = ""
    
    enum Field: Hashable {
        case name, character
    }
    @FocusState private var focusField: Field?
        
    var body: some View {
        BaseSheet2(
            title : "유저 정보 추가",
            text: searchResult.isEmpty ? "검색" : "추가",
            action: {
                if (searchResult.isEmpty) {
                    searchAction()
                } else {
                    initAction()
                }
            },
            enabled:(searchResult.isEmpty && !user.isEmpty && !searchCharacterName.isEmpty) || !searchResult.isEmpty,
            errorMsg: $errorMsg,
            dismiss: {
                dialogAction.hideDialog()
            }
        ) {
            VStack {
                Form {
                    Section(header : Text("유저 이름")) {
                        TextField("유저 이름 입력",text: $user)
                            .focused($focusField, equals: .name)
                            .submitLabel(.next)
                            .disabled(!searchResult.isEmpty)
                    }
                    Section(header : Text("대표 캐릭터")) {
                        TextField("대표 캐릭터 입력",text: $searchCharacterName)
                            .focused($focusField, equals: .character)
                            .submitLabel(.done)
                            .disabled(!searchResult.isEmpty)
                    }
                }            
                .scrollDisabled(true)
                .scrollContentBackground(.hidden)
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
            .frame(minHeight: 300)
            .onSubmit {
                switch focusField {
                case .name:
                    focusField = .character
                case .character:
                    focusField = nil
                case nil:
                    break
                }
            }
        }
    }
    
    private func searchAction() {
        dialogAction.dialogSearchCharacter(
            name: searchCharacterName,
            updateProgress: {
                showLoading = $0 as! Bool
            },
            updateList: {
                searchResult = $0
            },
            updateError: {
                errorMsg = $0
            }
        )
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
                dialogAction.hideDialog()
            }
            
        }
    }
}
