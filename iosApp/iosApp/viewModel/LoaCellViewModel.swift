//
//  LoaCellViewModel.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import shared
import SwiftUI
import SwiftUI_Snackbar
import Combine

class LoaCellViewModel: CommonViewModel, ObservableObject {
    
    let sc : SnackbarController = SnackbarController()

    @Published var roomList : [RoomInfo] = []
    @Published var user : FBUser? = nil
    @Published var roomId : String = ""
    @Published var totalRoomInfo : TotalRoomInfo = TotalRoomInfo.companion.getInit()
    @Published var showSplash :Bool = true
    @Published var defaultSpace : CGFloat = 20.0
    @Published var syncData : Bool = false
    @Published var showSetting :Bool = false
    override func closeSetting() {
        showSetting = false
    }
    
    override func getSetting() -> Bool {
        return showSetting
    }
    @Published var showLoading : Bool = false
    @Published var msg :String = ""
    @Published var baseUrl :String = ""
    
    
    var roomInfo :RoomInfo? {
        totalRoomInfo.roomInfo
    }
    var userInfo : shared.UserInfo? {
        totalRoomInfo.userInfo
    }
    var raidInfo : RaidInfo? {
        totalRoomInfo.raidInfo
    }
    var focusUserName : String {
        totalRoomInfo.focusUserName
    }
    var focusRaidId :String {
        totalRoomInfo.focusRaidId
    }
    var tabState : RoomState {
        totalRoomInfo.tabState
    }
    var dialogStatus : Modal? {
        totalRoomInfo.dialogState
    }
    var characterList : [Character] {
        totalRoomInfo.characterList
    }
    
    init() {
        super.init(
            dataBase: ModuleProvider().getAppDataBase(),
            config: ModuleProvider().getConfig(),
            loginHelper: ModuleProvider().getLoginHelper(),
            synergyReferenceHelper: ModuleProvider().getSynergyReferenceHelper()
        )

        loginHelper.auth.user.collect { user in
            self.user = user
            self.showSplash = false
        }
        syncDataFlow.collect { value in
            withAnimation {
                self.syncData = value!.boolValue
            }
        }
        roomListFlow.collect { value in
            self.roomList = value as! [RoomInfo]
        }
        roomIdFlow.collect { value in
            self.roomId = value! as String
        }
        totalRoomInfoFlow.collect { value in
            withAnimation {
                self.totalRoomInfo = value!
            }
        }
        showLoadingFlow.collect { value in
            withAnimation {
                self.showLoading = value as! Bool
            }
        }
        msgFlow.collect { value in
            self.msg = value! as String
        }
        sheetSpaceFlow.collect { value in
            self.defaultSpace = value as! CGFloat
        }
        defaultUrlFlow.collect { value in
            self.baseUrl = value! as String
        }
    }

    
    func fbUserIsAnonymous() -> KotlinBoolean? {
        guard let user = user else {
            return nil
        }
        return KotlinBoolean(bool: user.isAnonymous)
    }
    
    
    func getUserUid() -> String? {
        return user?.uid
    }
    
    
    func showRoomEnterError() {
        
    }
    
    override func showSnackBar(msg: String) {
        self.sc.showSnackBar(message: msg,label: "확인")
    }
    
    func showSnackBar(msg: String,label:String,action:@escaping () -> Void) {
        self.sc.showSnackBar(message: msg,label: label, perfromAction : action)
    }
    
    func resetSnackBar() {
        self.sc.resetSnackBar()
    }
    
}
