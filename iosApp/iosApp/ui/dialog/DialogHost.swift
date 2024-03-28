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
                .sheet(
                    isPresented: Binding(
                        get: {dialogStatus == DialogStatus.userAdd},
                        set: { _ in
                            dismiss()
                        }
                    )
                ){
                    AddUserSheet(dialogAction : dialogAction)
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
                            .modifier(dialog)
                        case DialogStatus.roomAdd:
                            AddRoomSheet(dismiss: dismiss) { title, description, password in
                                dialogAction.dialogRoomAdd(title: title, description: description, password: password)
                            }
                        case DialogStatus.roomEnter:
                            RoomEnterDialog(
                                nowEnterRoomList: dialogAction.getRoomListToUniqueId(),
                                dismiss: dismiss
                            ) { roomId, roomInfo in
                                dialogAction.dialogRoomEnter(roomId: roomId, roomInfo: roomInfo)
                            }
                            .modifier(dialog)
                            
                        case DialogStatus.roomEnterByScheme:
                            RoomEnterDialog(
                                nowEnterRoomList: dialogAction.getRoomListToUniqueId(),
                                schemeData : dialogAction.getSchemeData(),
                                dismiss: dismiss
                            ) { roomId, roomInfo in
                                dialogAction.dialogRoomEnterByScheme(roomId: roomId, roomInfo: roomInfo)
                            }
                            .modifier(dialog)
                        case DialogStatus.roomEnterError:
                            RoomEnterErrorDialog {
                                dialogAction.dialogRoomEnterError()
                            }
                            .modifier(dialog)
                        case DialogStatus.roomExit:
                            RoomExitDialog(dismiss: dismiss) {
                                dialogAction.dialogRoomExit()
                            }
                            .modifier(dialog)
                        case DialogStatus.roomEdit:
                            let info = dialogAction.getRoomInfo()
                            EditRoomSheet(dismiss: dismiss, title: info.title, desctiption: info.description_, password: info.enterPassword) { title, description, password in
                                dialogAction.dialogRoomEdit(title: title, description: description, password: password)
                            }
                        case DialogStatus.userAdd:
                            EmptyView()
                        case DialogStatus.raidAdd:
                            AddRaidSheet { fbRaidInfo in
                                dialogAction.dialogRaidAdd(fbRaidInfo : fbRaidInfo)
                            }
                        case DialogStatus.raidEdit:
                            EditRaidSheet(
                                raidInfo : dialogAction.getRaidInfo()
                            ) { fbRaidInfo in
                                dialogAction.dialogRaidEdit(fbRaidInfo : fbRaidInfo)
                            }
                        case DialogStatus.raidFilter:
                            FilterSheet(
                                totalRoomInfo: dialogAction.getTotalRoomInfo()
                            ) { filter in
                                dialogAction.dialogFilterUpdate(filter: filter)
                            }
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
                            ProfileNameDialog(
                                nowName: dialogAction.getDisplayName(),
                                dismiss: dismiss
                            ) { name in
                                dialogAction.dialogEditName(name: name)
                            }
                            .modifier(dialog)
                        case DialogStatus.shareSheet:
                            ShareSheet(roomInfo: dialogAction.getRoomInfo())
                        case DialogStatus.testSheet:
                            BaseSheet(
                                title: "여백 테스트",
                                text: "확인",
                                action: dismiss,
                                enabled: true,
                                errorMsg: .constant("")
                            ) {
                                Text("테스트 문구")
                            }
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
