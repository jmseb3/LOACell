//
//  SettingDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/27.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ProfileNameDialog: View {
    @State private var name :String
    let dialogAction :DialogAction

    init(dialogAction : DialogAction) {
        self.dialogAction = dialogAction
        self.name = dialogAction.getDisplayName()
    }
    
    var body: some View {
        BaseDialog(
            title: Dialog.characterEdit.title,
            leftText: "취소", 
            rightText: "변경",
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                if name.isNotEmpty {
                    dialogAction.dialogEditName(name: name)
                }
            },
            rightEnabled: .constant(name != "")
        ) {
            VStack {
                LengthLimitTextField(maxLength: 12, placeHolder: "이름을 입력해주세요.", text: $name)
            }
        }
    }
}

