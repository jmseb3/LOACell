//
//  DialogHost.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

enum DialogSwift {
    init?(modal :Modal) {
        if modal is Dialog {
            self.init(dialog: (modal as! Dialog))
        } else {
            return nil
        }
    }
    
    init?(dialog : Dialog) {
        switch dialog {
        case Dialog.roomAction:
            self = .RoomAction
        case Dialog.roomEnter:
            self = .RoomEnter
        case Dialog.roomEnterByScheme:
            self = .RoomEnterByScheme
        case Dialog.roomEnterError:
            self = .RoomEnterError
        case Dialog.roomExit:
            self = .RoomExit
        case Dialog.roomExit:
            self = .RoomExit
        case Dialog.raidDelete:
            self = .RaidDelete
        case Dialog.raidUserDelete:
            self = .RaidUserDelete
        case Dialog.characterEdit:
            self = .CharacterEdit
        case Dialog.characterDelete:
            self = .CharacterDelete
        case Dialog.settingEditName:
            self = .SettingEditName
        default:
            return nil
        }
    }
    
    case RoomAction
    case RoomEnter
    case RoomEnterByScheme
    case RoomEnterError
    case RoomExit
    case RaidDelete
    case RaidUserDelete
    case CharacterEdit
    case CharacterDelete
    case SettingEditName
}

struct ModalHost<Content: View>: View {
    private let dialogStatus : Modal?
    private let dialogAction :DialogAction
    private let content: () -> Content
    
    public init(
        dialogStatus: Modal?,
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
                ZStack {
                    Color(.white)
                    VStack {
                        content()
                    }
                }
                .modifier(ModalSheet(gemetryReader: gemetryReader, dialogStatus: dialogStatus, dialogAction: dialogAction))
            }
        }
    }
}

struct ModalSheet: ViewModifier {
    let gemetryReader : GeometryProxy
    let dialogStatus : Modal?
    let dialogAction :DialogAction
    
    var dialog: some View {
        VStack {
            switch(DialogSwift(dialog: dialogStatus as! Dialog)) {
            case .RoomAction:
                RoomActionDialog(dialogAction: dialogAction)
            case .RoomEnter:
                RoomEnterDialog(dialogAction: dialogAction)
            case .RoomEnterByScheme:
                RoomEnterDialog(dialogAction: dialogAction, schemeData: dialogAction.getSchemeData())
            case .RoomEnterError:
                RoomEnterErrorDialog(dialogAction: dialogAction)
            case .RoomExit:
                RoomExitDialog(dialogAction: dialogAction)
            case .RaidDelete:
                DeleteRaidDialog(dialogAction: dialogAction)
            case .RaidUserDelete:
                DeleteRaidUserDialog(dialogAction: dialogAction)
            case .CharacterEdit:
                EditCharacterDialog(dialogAction: dialogAction)
            case .CharacterDelete:
                DeleteCharacterDialog(dialogAction: dialogAction)
            case .SettingEditName:
                ProfileNameDialog(dialogAction: dialogAction)
            default:
                EmptyView()
            }
        }
        
    }
    
    var sheet : some View {
        VStack {
            switch(dialogStatus as! Sheet) {
            case Sheet.userAdd:
                AddUserSheet(dialogAction : dialogAction)
            case Sheet.roomAdd:
                AddRoomSheet(dialogAction : dialogAction)
            case Sheet.roomEdit:
                EditRoomSheet(dialogAction : dialogAction)
            case Sheet.raidAdd:
                AddRaidSheet(dialogAction : dialogAction)
            case Sheet.raidEdit:
                EditRaidSheet(dialogAction : dialogAction)
            case Sheet.raidFilter:
                FilterSheet(dialogAction : dialogAction)
            case Sheet.raidUserAdd:
                AddRaidUserSheet(dialogAction : dialogAction)
            case Sheet.shareSheet:
                ShareSheet(dialogAction : dialogAction)
            case Sheet.testSheet:
                BaseSheet(
                    title: "여백 테스트",
                    text: "확인",
                    action: {
                        dialogAction.hideDialog()
                    },
                    enabled: true,
                    errorMsg: .constant(""),
                    dismiss : {
                        dialogAction.hideDialog()
                    }
                ) {
                    Text("테스트 문구")
                }
            default:
                EmptyView()
            }
        }
    }
    
    func body(content: Content) -> some View {
        ZStack {
            content
            if dialogStatus is Dialog {
                ZStack {
                    Color(.black)
                        .opacity(0.5)
                        .onTapGesture {
                            dialogAction.hideDialog()
                        }
                        .ignoresSafeArea()
                }
                dialog
                    .padding()
                    .frame(width: gemetryReader.size.width * 0.8 , alignment: .center)
                    .position(x: gemetryReader.size.width / 2, y : gemetryReader.size.height / 2)
            }
        }
        .sheet(
            isPresented:
                Binding(
                    get: {dialogStatus is Sheet},
                    set: { _ in dialogAction.hideDialog() }
                )
        ) {
            sheet
        }
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
