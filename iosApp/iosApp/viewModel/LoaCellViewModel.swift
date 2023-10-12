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
import SwiftUI_Snackbar

class LoaCellViewModel: ObservableObject, ViewModelImpl {
        
    
    @Published var logginIn :Bool = false
    @Published var roomList : [RoomInfo] = []
    @Published var user : User? = nil
    @Published var roomId : String = ""
    @Published var totalRoomInfo : TotalRoomInfo = TotalRoomInfo(roomInfo: nil, raidInfoList: [], userInfoList: [])
    
    @Published var focusUserName : String = ""
    @Published var userInfo : shared.UserInfo? = nil
    
    @Published var focusRaidId : String = ""
    @Published var raidInfo : shared.RaidInfo? = nil
    
    @Published var syncData : Bool = false
    @Published var showSetting :Bool = false
    
    let sc : SnackbarController = SnackbarController()
    
    lazy var config :Config = Config()
    lazy var db : AppDataBase = AppDataBase(driverFactory: DriverFactory())
    lazy var commonViewModel : CommonViewModel = CommonViewModel(coroutineScope: nil, dataBase: db, config: config, viewModelImpl: self)

    init() {
        Auth.auth().addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
        }
        commonViewModel.syncData.collect { value in
            self.syncData = value!.boolValue
        }
        commonViewModel.roomList.collect { value in
            self.roomList = value as! [RoomInfo]
        }
        commonViewModel.roomId.collect { value in
            self.roomId = value! as String
        }
        commonViewModel.totalRoomInfo.collect { value in
            self.totalRoomInfo = value!
        }
        commonViewModel.focusUserName.collect { value in
            self.focusUserName = value! as String
        }
        commonViewModel.userInfo.collect { value in
            self.userInfo = value
        }
        commonViewModel.focusRaidId.collect { value in
            self.focusRaidId = value! as String
        }
        commonViewModel.raidInfo.collect { value in
            self.raidInfo = value
        }
    }
    func closeLoading() {
        
    }
    
    func closeSetting() {
        showSetting = false
    }
    
    func fbUserIsAnonymous() -> KotlinBoolean? {
        return KotlinBoolean(bool: true)
    }
    
    func getSetting() -> Bool {
        return showSetting
    }
    
    func getUserUid() -> String? {
        return nil
    }
    
    func syncStart(force:Bool = false) {
        guard let userUid = user?.uid else {
            return
        }
        commonViewModel.syncStart(uid: userUid , force:force)
    }
    

    func showRoomEnterError() {
        
    }
    
    func showSnackBar(msg: String) {
        self.sc.showSnackBar(message: msg,label: "확인")
    }
    
    func resetSnackBar() {
        self.sc.resetSnackBar()
    }
}
