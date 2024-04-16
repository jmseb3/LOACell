//
//  RaidDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/14.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DeleteRaidDialog :  View {
    let dialogAction :DialogAction
    
    var body: some View {
        DeleteDialog(
            title: Dialog.raidDelete.title,
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                dialogAction.dialogRaidDelete()
            },
            rightEnabled: .constant(true)
        ) {
            Text("레이드 정보를 삭제 하시겠습니까?")
                .font(.system(size: 13))
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

struct DeleteRaidUserDialog :  View {
    let dialogAction :DialogAction
    
    var body: some View {
        DeleteDialog(
            title: Dialog.raidUserDelete.title,
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                dialogAction.dialogUserDelete()
            },
            rightEnabled: .constant(true)
        ) {
            Text("선택하신 캐릭터를 파티에서 삭제 하시겠습니까?")
                .font(.system(size: 13))
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
