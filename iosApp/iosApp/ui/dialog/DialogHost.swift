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
                            EmptyView()
                        case DialogStatus.roomAdd:
                            EmptyView()
                        case DialogStatus.roomEnter:
                            EmptyView()
                        case DialogStatus.roomEnterError:
                            EmptyView()
                        case DialogStatus.roomExit:
                            EmptyView()
                        case DialogStatus.roomEdit:
                            EmptyView()
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
                            EmptyView()
                        case DialogStatus.raidFilter:
                            EmptyView()
                        case DialogStatus.raidDelete:
                            EmptyView()
                        case DialogStatus.raidUserAdd:
                            EmptyView()
                        case DialogStatus.raidUserDelete:
                            EmptyView()
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
