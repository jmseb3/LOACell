//
//  RoomSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct AddRoomSheet :View {
    let dismiss :() -> Void
    let addAction : (_ title: String, _ description: String, _ password: String) -> Void

    var body: some View {
        RoomSheetBase(sheetTitle: "방 만들기", addAction: addAction)
    }
}

struct EditRoomSheet :View {
    let dismiss :() -> Void
    let title :String
    let desctiption :String
    let password :String
    let addAction : (_ title: String, _ description: String, _ password: String) -> Void

    var body: some View {
        RoomSheetBase(
            buttonText: "수정",
            sheetTitle: "수정하기",
            title: title,
            desctiption: desctiption,
            password: password,
            addAction: addAction
        )
    }
}

struct RoomSheetBase: View {
    
    var buttonText :String = "추가"
    let sheetTitle :String
    
    @State var title :String = ""
    @State var desctiption : String = ""
    @State var password :String = ""
    @State private var usePassword :Bool = false
    @State private var errMsg :String = ""
    
    let addAction : (_ title: String, _ description: String, _ password: String) -> Void
    
    var body: some View {
        BaseSheet(
            title: sheetTitle,
            text: buttonText,
            action: {
                addAction(title,desctiption,password)
            },
            enabled: title.isNotEmpty && (!usePassword || (usePassword && password.isNotEmpty)),
            errorMsg: $errMsg
        ) {
            VStack {
                LengthLimitTextField(maxLength: 10, placeHolder: "제목을 입력하세요.", text: $title)
                LengthLimitTextField(maxLength: 100, placeHolder: "방 설명을 입력하세요.", text: $desctiption)
                CheckButton(isOn: usePassword, text: "패스워드 사용") {
                    withAnimation {
                        usePassword = !usePassword
                    }
                }
                if usePassword {
                    LengthLimitTextField(maxLength: 10, placeHolder: "비밀번호를 입력하세요.", text: $password)
                }
            }
        }
    }
}
