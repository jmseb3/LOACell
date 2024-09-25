//
//  RoomDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RoomActionDialog: View {
    let dialogAction :DialogAction

    var body: some View {
        BaseDialogNoButton(title: Dialog.roomEnter.title) {
            HStack{
                RoomEnterButton(
                    resource: \.room_enter,
                    text: "입장하기"
                ){
                    dialogAction.dialogRoomAction(status: ModalConst().ROOM_ACTION_ENTER)
                }
                
                RoomEnterButton(
                    resource: \.room_make,
                    text: "방만들기"
                ){
                    dialogAction.dialogRoomAction(status: ModalConst().ROOM_ACTION_ADD)
                }
            }
        }
    }
}

struct RoomEnterButton :View {
    let resource : KeyPath<SharedRes.images, shared.ImageResource>
    let text :String
    let confirm : () -> Void
    
    var body: some View {
        Button {
            confirm()
        } label: {
            VStack{
                Image(resource: resource)
                Text(text)
                    .foregroundColor(.black)
            }
            .frame(width: 100,height: 100)
            .overlay {
                RoundedRectangle(cornerRadius: 15)
                    .stroke(.black,lineWidth: 2)
            }
            
        }
        .padding()
        .cornerRadius(15)
    }
}

struct RoomEnterErrorDialog : View {
    let dialogAction :DialogAction
    
    var body: some View {
        BaseDialog(
            title: Dialog.roomEnterError.title,
            leftText: nil,
            rightText: "확인",
            rightAction: {
                dialogAction.dialogRoomEnterError()
            },
            rightEnabled: .constant(true)
        ) {
            Text("해당 방에 입장 권한이 없거나 삭제되었습니다.")
        }
    }
}

struct RoomEnterDialog :View {
    private let nowEnterRoomList : [String]
    private let schemeData :SchemeData?
    private let dismiss : () -> Void
    private let confirm :(_ roomId :String,_ roomInfo :FBRoomInfo) -> Void
    
    @State private var roomId :String = ""
    @State private var errorMsg :String = ""
    @State private var nowRoomInfo :FBRoomInfo? = nil
    @State private var password : String = ""
    @State private var enterPassword :String = ""
    
    init(
        dialogAction : DialogAction
    ) {
        self.nowEnterRoomList = dialogAction.getRoomListToUniqueId()
        self.schemeData = nil
        self.dismiss = {
            dialogAction.hideDialog()
        }
        self.confirm = { roomId, roomInfo in
            dialogAction.dialogRoomEnter(roomId: roomId, roomInfo: roomInfo)
        }
    }
    
    init(
        dialogAction : DialogAction,
        schemeData :SchemeData?
    ) {
        self.nowEnterRoomList = dialogAction.getRoomListToUniqueId()
        self.schemeData = schemeData
        self.dismiss = {
            dialogAction.hideDialog()
        }
        self.confirm = { roomId, roomInfo in
            dialogAction.dialogRoomEnterByScheme(roomId: roomId, roomInfo: roomInfo)
        }
    }
    
    var enabled : Bool {
        password.isEmpty ? roomId.count == 20 : true
    }
    var body: some View {
        BaseDialog(
            title: password.isEmpty ? "입장하기" : "비밀번호 입력",
            leftText: "취소",
            rightText: password.isEmpty ? "입장" : "확인",
            leftAction: {
                dismiss()
            },
            rightAction: {
                if password.isEmpty {
                    if roomId.isEmpty {
                        errorMsg = "ID를 입력해주세요."
                    } else {
                        if nowEnterRoomList.contains(roomId) {
                            errorMsg = "이미 입장한 방입니다."
                        } else {
                            CommonRoomHelper().checkExist(
                                roomId: roomId) { roomInfo in
                                    if roomInfo.enterPassword.isEmpty {
                                        confirm(roomId,roomInfo)
                                    } else {
                                        withAnimation {
                                            nowRoomInfo = roomInfo
                                            password = roomInfo.enterPassword
                                        }
                                    }
                                } failAction: {
                                    errorMsg = "방이 존재 하지 않습니다."
                                }
                            
                        }
                    }
                    
                } else {
                    if password == enterPassword {
                        confirm(roomId,nowRoomInfo!)
                    } else {
                        errorMsg = "비밀번호가 맞지 않습니다."
                    }
                }
            },
            rightEnabled: .constant(enabled)
        ) {
            VStack {
                LengthLimitTextField(maxLength: 20, placeHolder: "방 ID를 입력해주세요.", text: $roomId)
                if password.isNotEmpty {
                    LengthLimitTextField(maxLength: 10, placeHolder: "방 비밀번호를 입력해주세요", text: $enterPassword)
                }
                if errorMsg.isNotEmpty {
                    Text(errorMsg)
                        .foregroundColor(.red)
                        .animation(.spring, value: errorMsg)
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                errorMsg = ""
                            }
                        }
                }
            }.onAppear{
                if let scheme = schemeData {
                    self.roomId = scheme.roomId
                    self.nowRoomInfo = scheme.fbRoomInfo
                    self.password = scheme.fbRoomInfo.enterPassword
                }
            }
        }
    }
    
}

struct RoomExitDialog : View {
    let dialogAction :DialogAction

    var body: some View {
        BaseDialog(
            title: Dialog.roomExit.title,
            leftText: "취소",
            rightText: "나가기",
            leftAction: {
                dialogAction.hideDialog()
            },
            rightAction: {
                dialogAction.dialogRoomExit()
            },
            rightEnabled: .constant(true)
        ) {
            Text("방에서 나가시겠습니까?.")
        }
    }
}

