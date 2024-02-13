//
//  SettingDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/27.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct ProfileNameDialog: View {
    @State var nowName :String
    let dismiss : () -> Void
    let success : (_ name :String) -> Void
    
    var body: some View {
        BaseDialog(
            title: "이름 변경",
            leftText: "취소", 
            rightText: "변경",
            leftAction: {
                self.dismiss()
            },
            rightAction: {
                if nowName.isNotEmpty {
                    self.success(nowName)
                }
            },
            rightEnabled: .constant(nowName != "")
        ) {
            VStack {
                LengthLimitTextField(maxLength: 12, placeHolder: "이름을 입력해주세요.", text: $nowName)
            }
        }
    }
}

