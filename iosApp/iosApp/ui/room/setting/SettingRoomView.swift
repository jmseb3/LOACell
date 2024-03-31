//
//  SettingRoomView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/28.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SettingRoomView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    var result : [FBDataItem] {
        viewModel.tempOfFBData
    }
    var user :FBUser? {
        viewModel.user
    }
    var totalRoomInfo : TotalRoomInfo {
        viewModel.totalRoomInfo
    }
    var roomInfo : RoomInfo? {
        totalRoomInfo.roomInfo
    }
    var raidList : [RaidInfo] {
        totalRoomInfo.raidInfoList
    }
    var userList : [UserInfo] {
        totalRoomInfo.userInfoList
    }
    
    @State private var fetch :Bool = false
    
    var body: some View {
        ScrollView {
            
            VStack {
                if let room = roomInfo {
                    SettingRoomInfo(
                        roomInfo: room,
                        raidList : raidList,
                        userList : userList
                    )
                    Divider()
                    UserUidList(fetch: $fetch, roomInfo: room, result: result, ownerChangeSuccess: {
                        viewModel.hideRoomInfo()
                    }) { err in
                        viewModel.showSnackBar(msg: "변경에 실패했습니다.\(err.errorMsg)")
                    }
                    
                }
            }.onAppear {
                if roomInfo != nil {
                    if user != nil  {
                        if roomInfo!.getAllUidList().count == 1 && roomInfo!.owner == user!.uid {
                            viewModel.tempOfFBData = [FBDataItem(uid: user!.uid, displayName: user!.displayName ,photoURL: user!.photoUrl)]
                        }
                    }
                    
                    if roomInfo!.getAllUidList() == result.map({ item in item.uid}) {
                        fetch = true
                    }
                    
                    if !fetch {
                        DispatchQueue.main.async {
                            roomInfo!.checkNotExistUid { data in
                                fetch = true
                                viewModel.tempOfFBData = data
                            } completionHandler: { error in
                                print(error)
                            }
                            
                        }
                    }
                }
            }
        }
    }
}

struct SettingRoomInfo: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    let roomInfo : RoomInfo
    let raidList : [RaidInfo]
    let userList : [UserInfo]
    @State private var showPassWord :Bool = false
    @State private var showExit :Bool = false
    
    private var otherMemberList :[String] {
        roomInfo.enterUser + roomInfo.editableUser
    }
    
    private var passString :String {
        if !showPassWord {
            "**********"
        } else {
            roomInfo.enterPassword
        }
    }
    var body: some View {
        VStack {
            SectionCardView(
                title: "방 정보",
                iconSrc: \.edit,
                iconAction: {
                    viewModel.showDialog(modal: Sheet.roomEdit)
                }
            ) {
                VStack(alignment:.leading) {
                    Button {
                        if raidList.isEmpty && userList.isEmpty {
                            showExit = true
                        } else {
                            viewModel.showSnackBar(msg: "레이드 정보/유저 정보를 모두 삭제해주세요.")
                        }
                    } label: {
                        Text("나가기")
                    }.disabled(otherMemberList.count != 0)
                        .alert("나가기", isPresented: $showExit) {
                            Button("나기기",role:.destructive) {
                                CommonRoomHelper().deleteRoom(
                                    roomId: roomInfo.uniqueId,
                                    successAction: {
                                        showExit = false
                                        viewModel.deleteRoom(roomId: roomInfo.uniqueId)
                                    },
                                    failAction: {error in
                                        viewModel.showSnackBar(msg: "나가기에 실패했습니다. 관리자에게 문의하세요\(error.errorMsg)")
                                        showExit = false
                                    })
                            }
                        } message: {
                            Text("정말 해당 방에서 나갈까요?\n삭제된 데이터는 복구가 불가능합니다.")
                        }
                    Divider()
                    VStack(alignment:.leading) {
                        Text("제목")
                        Text(roomInfo.title)
                        Text("설명")
                        Text(roomInfo.description_)
                    }
                    if roomInfo.enterPassword.isNotEmpty {
                        HStack {
                            VStack(alignment:.leading) {
                                Text("비밀번호")
                                Text(passString)
                            }
                            Spacer()
                            IconButton(resource: showPassWord ? \.visible_off : \.visible_on) {
                                showPassWord = !showPassWord
                            }
                        }
                    }
                }
                .padding(5)
            }.padding(5)
        }
    }
    
}

struct UserUidList : View {
    @Binding var fetch : Bool
    let roomInfo : RoomInfo
    let result : [FBDataItem]
    let ownerChangeSuccess :() ->Void
    let ownerChangeFail : (_ err :Error) -> Void
    
    private var group : [String:[String]] {
        [
            RoomRole.owner.toName : [roomInfo.owner],
            RoomRole.manager.toName : roomInfo.editableUser,
            RoomRole.user.toName : roomInfo.enterUser
        ]
    }
    var body: some View {
        ZStack {
            if !fetch {
                LoadingView(
                    info: "유저 정보를 가져옵니다.",
                    color: Color.white.opacity(0.3)
                )
            } else {
                SectionCardView(
                    title:"사용자 정보"
                ) {
                    LazyVStack(alignment: .leading) {
                        ForEach([RoomRole.owner.toName,RoomRole.manager.toName,RoomRole.user.toName], id: \.self) { name in
                            let items = group[name]!
                            if items.count != 0 {
                                Section(header:
                                            Text(name)
                                    .background(Color.gray)
                                    .frame(maxWidth: .infinity,alignment:.leading)
                                ) {
                                    ForEach(items ,id: \.self) { uid in
                                        UserUidItem(roomId: roomInfo.uniqueId, name: name, uid: uid, fbData: result) {
                                            CommonRoomHelper().changeOwner(
                                                roomId: roomInfo.uniqueId,
                                                preOwner: roomInfo.owner,
                                                newOwnerUid: uid,
                                                commonAction : {},
                                                successAction: ownerChangeSuccess,
                                                failAction: ownerChangeFail
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                }
                .padding(5)
            }
        }
    }
}

struct UserUidItem : View {
    let roomId :String
    let name :String
    let uid :String
    let fbData : [FBDataItem]
    let changeOwner :() -> Void
    
    @State private var showConfirm :Bool = false
    @State private var showChange :Bool = false

    private var find : FBDataItem? {
        get {
            var result :FBDataItem? = nil
            fbData.forEach { data in
                if result == nil {
                    if data.uid  == uid {
                        result = data
                    }
                }
            }
            return result
        }
        
    }
    var body: some View {
        HStack {
            Text(find?.getName() ?? uid)
            Spacer()
            if name != RoomRole.owner.toName {
                Button {
                    showChange = true
                } label: {
                    Text("위임")
                }.alert("소유자 위임", isPresented: $showChange) {
                    Button("소유자 위임",role:.destructive) {
                        changeOwner()
                        showChange = false
                    }
                } message: {
                    Text("해당 유저에게 소유자권한을 위임 하시겠습니까?")
                }
                IconButton(
                    resource: \.room_exit,
                    iconSize: 22
                ) {
                    showConfirm = true
                }
                .alert("내보내기", isPresented: $showConfirm) {
                    Button("내보내기",role:.destructive) {
                        if name == RoomRole.manager.toName {
                            CommonRoomHelper().exitEditableUserFromRoom(roomId: roomId, editableUser: [uid]) {
                                showConfirm = false
                            }
                        } else if (name == RoomRole.user.toName) {
                            CommonRoomHelper().exitEnterUserFromRoom(roomId: roomId, enterUser: [uid]) {
                                showConfirm = false
                            }
                        }
                    }
                } message: {
                    Text("해당 유저를 방에서 정말 내보내시겠습니까?")
                }
            }
        }
    }
}
