//
//  RoomSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct AddRoomSheet :View {
    let dialogAction : DialogAction

    var body: some View {
        RoomSheetBase(
            dialogAction : dialogAction,
            sheetTitle: Sheet.roomAdd.title
        )
    }
}

struct EditRoomSheet :View {
    let dialogAction : DialogAction
    var body: some View {
        RoomSheetBase(
            dialogAction : dialogAction,
            sheetTitle: Sheet.roomEdit.title,
            buttonText: "수정",
            roomInfo : dialogAction.getRoomInfo()
        )
    }
}

struct RoomSheetBase: View {
    
    private let sheetTitle :String
    private var buttonText :String = "추가"

    @State private var title :String = ""
    @State private var desctiption : String = ""
    @State private var password :String = ""
    @State private var usePassword :Bool = false
    @State private var errMsg :String = ""
    
    private let addAction : (_ title: String, _ description: String, _ password: String) -> Void
    private let dismiss : () -> Void
    
    init(
        dialogAction : DialogAction,
        sheetTitle: String
    ) {
        self.sheetTitle = sheetTitle
        self.addAction = {title, description, password in
            dialogAction.dialogRoomAdd(title: title, description: description, password: password)
        }
        self.dismiss = {
            dialogAction.hideDialog()
        }
    }
    init(
        dialogAction : DialogAction,
        sheetTitle: String,
        buttonText: String,
        roomInfo : RoomInfo
    ) {
        self.sheetTitle = sheetTitle
        self.buttonText = buttonText
        self.title = roomInfo.title
        self.desctiption = roomInfo.description_
        self.password = roomInfo.enterPassword
        self.addAction = {title, description, password in
            dialogAction.dialogRoomEdit(title: title, description: description, password: password)
        }
        self.dismiss = {
            dialogAction.hideDialog()
        }
    }
    
    var body: some View {
        BaseSheet2(
            title: sheetTitle,
            text: buttonText,
            action: {
                addAction(title,desctiption,password)
            },
            enabled: title.isNotEmpty && (!usePassword || (usePassword && password.isNotEmpty)),
            errorMsg: $errMsg,
            dismiss: dismiss
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
