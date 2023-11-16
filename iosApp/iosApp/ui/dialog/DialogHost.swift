//
//  DialogHost.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DialogHost<Content: View>: View {
    private let dialogStatus : DialogStatus
    private let dialogAction :DialogAction
    private let content: () -> Content
    
    public init(
        dialogStatus: DialogStatus,
        dialogAction : DialogAction,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.dialogStatus = dialogStatus
        self.dialogAction = dialogAction
        self.content = content
    }
    
    var heightFactor: CGFloat {
        UIScreen.main.bounds.height > 800 ? 3.6 : 3
    }
    
    func dismiss() {
        dialogAction.hideDialog()
    }
    
    var body: some View {
        ZStack {
            GeometryReader {gemetryReader in
                let dialog = Dialog(gemetryReader: gemetryReader)
                ZStack {
                    Color(.white)
                    VStack {
                        content()
                    }
                }
                if dialogStatus != DialogStatus.none {
                    ZStack {
                        Color(.black)
                            .opacity(0.5)
                            .onTapGesture {
                                dismiss()
                            }
                            .ignoresSafeArea()
                        switch(dialogStatus){
                        case DialogStatus.roomAction:
                            RoomActionDialog(dismiss: dismiss) { status in
                                dialogAction.dialogRoomAction(status: status)
                            }
                        case DialogStatus.roomAdd:
                            AddRoomSheet(dismiss: dismiss) { title, description, password in
                                dialogAction.dialogRoomAdd(title: title, description: description, password: password)
                            }
                        case DialogStatus.roomEnter:
                            EmptyView()
                        case DialogStatus.roomEnterError:
                            EmptyView()
                        case DialogStatus.roomExit:
                            EmptyView()
                        case DialogStatus.roomEdit:
                            let info = dialogAction.getRoomInfo()
                           EditRoomSheet(dismiss: dismiss, title: info.title, desctiption: info.description_, password: info.enterPassword) { title, description, password in
                               dialogAction.dialogRoomEdit(title: title, description: description, password: password)
                           }
                        case DialogStatus.userAdd:
                            AddUserSheet(
                                roomId : dialogAction.getRoomInfoUniqueId()
                            ) {
                                dismiss()
                            }
                        case DialogStatus.raidAdd:
                            AddRaidSheet(
                                roomId : dialogAction.getRoomInfoUniqueId()
                            ) { fbRaidInfo in
                                dialogAction.dialogRaidAdd(fbRaidInfo : fbRaidInfo)
                            }
                        case DialogStatus.raidEdit:
                            EditRaidSheet(
                                raidInfo : dialogAction.getRaidInfo()
                            ) { fbRaidInfo in
                                dialogAction.dialogRaidEdit(fbRaidInfo : fbRaidInfo)
                            }
                        case DialogStatus.raidFilter:
                            EmptyView()
                        case DialogStatus.raidDelete:
                            DeleteRaidDialog(dismiss: dismiss) {
                                dialogAction.dialogRaidDelete()
                            }
                            .modifier(dialog)
                        case DialogStatus.raidUserAdd:
                            let userAndCharacterMap = dialogAction.getUserAndCharacterMap()
                            if !userAndCharacterMap.isEmpty {
                                AddRaidUserSheet(userAndCharacterMap: userAndCharacterMap) { character in
                                    dialogAction.dialogUserAdd(character : character)
                                }
                            }

                        case DialogStatus.raidUserDelete:
                            DeleteRaidUserDialog(dismiss : dismiss) {
                                dialogAction.dialogUserDelete()
                            }
                            .modifier(dialog)
                        case DialogStatus.characterEdit:
                            EditCharacterDialog(
                                userInfo: dialogAction.getUserInfo(),
                                characterList: dialogAction.getCharacterList(),
                                dismiss: dismiss
                            ) { name in
                                dialogAction.dialogEditName(name: name)
                            }
                            .modifier(dialog)
                        case DialogStatus.characterDelete:
                            DeleteCharacterDialog(
                                name: dialogAction.getUserInfo().name,
                                dismiss: dismiss
                            ) {
                                dialogAction.dialogCharacterDelete()
                            }
                            .modifier(dialog)
                            
                        case DialogStatus.settingEditName:
                            EmptyView()
                        case DialogStatus.shareSheet:
                            EmptyView()
                        default:
                            EmptyView()
                        }
                    }
                    .animation(.spring, value: dialogStatus)
                }
            }
        }
    }
}


struct Dialog: ViewModifier {
    let gemetryReader : GeometryProxy
    func body(content: Content) -> some View {
        content
            .padding()
            .frame(width: gemetryReader.size.width * 0.8 , alignment: .center)
            .position(x: gemetryReader.size.width / 2, y : gemetryReader.size.height / 2)
        
    }
}

struct RoundedCorner: Shape {
    
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
