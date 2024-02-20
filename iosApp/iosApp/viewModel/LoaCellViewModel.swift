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

class LoaCellViewModel: ObservableObject, ViewModelImpl {
    
    let sc : SnackbarController = SnackbarController()
    
    lazy var db : AppDataBase = AppDataBase(driverFactory: DriverFactory())
    lazy var config :Config = Config()
    lazy var loginHelper :LoginHelper = LoginHelper()
    private lazy var common : CommonViewModel = CommonViewModel(coroutineScope: nil, dataBase: db, config: config, loginHelper: loginHelper, viewModelImpl: self)
    
    @Published var roomList : [RoomInfo] = []
    @Published var user : FBUser? = nil
    
    @Published var roomId : String = ""
    @Published var totalRoomInfo : TotalRoomInfo = TotalRoomInfo.companion.getInit()
    @Published var showSplash :Bool = true
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
    var dialogStatus : DialogStatus {
        totalRoomInfo.dialogState
    }
    var characterList : [Character] {
        totalRoomInfo.characterList
    }
    @Published var defaultSpace : CGFloat = 20.0
    
    var myRole : RoomRole {
        totalRoomInfo.getMyRole(uid: user?.uid)
    }
    
    var tempOfFBData : [FBDataItem] {
        get {
            common.tempOfFBData
        }
        set(value) {
            common.tempOfFBData = value
        }
    }
    
    @Published var syncData : Bool = false
    @Published var showSetting :Bool = false
    
    @Published var showLoading : Bool = false
    @Published var msg :String = ""
    
    init() {
        loginHelper.auth.user.collect { user in
            self.user = user
            self.showSplash = false
        }
        common.syncData.collect { value in
            withAnimation {
                self.syncData = value!.boolValue
            }
        }
        common.roomList.collect { value in
            self.roomList = value as! [RoomInfo]
        }
        common.roomId.collect { value in
            self.roomId = value! as String
        }
        common.totalRoomInfo.collect { value in
            withAnimation {
                self.totalRoomInfo = value!
            }
        }
        common.showLoading.collect { value in
            withAnimation {
                self.showLoading = value as! Bool
            }
        }
        common.msg.collect { value in
            self.msg = value! as String
        }
        common.sheetSpace.collect { value in
            self.defaultSpace = value as! CGFloat
        }
    }
    
    func closeSetting() {
        showSetting = false
    }
    
    
    func fbUserIsAnonymous() -> KotlinBoolean? {
        guard let user = user else {
            return nil
        }
        return KotlinBoolean(bool: user.isAnonymous)
    }
    
    func getSetting() -> Bool {
        return showSetting
    }
    
    func getUserUid() -> String? {
        return user?.uid
    }
    
    func syncStart(force:Bool = false) {
        guard let userUid = user?.uid else {
            return
        }
        common.syncStart(uid: userUid , force:force)
    }
    func syncStartForce(uuid:String) {
        common.syncStart(uid: uuid , force:true)
    }
    
    
    func showRoomEnterError() {
        
    }
    
    func showSnackBar(msg: String) {
        self.sc.showSnackBar(message: msg,label: "확인")
    }
    
    func showSnackBar(msg: String,label:String,action:@escaping () -> Void) {
        self.sc.showSnackBar(message: msg,label: label, perfromAction : action)
    }
    
    func resetSnackBar() {
        self.sc.resetSnackBar()
    }
    
    func showDialog(dialogStatus: DialogStatus) {
        common.dialogAction.showDialog(dialogStatus: dialogStatus)
    }
    func hideDialog() {
        common.dialogAction.hideDialog()
    }
    
    func topBackAction() {
        common.topBackAction()
    }
    
    func bottomAddAction() {
        common.bottomAddAction()
    }
    
    func setNowRaidInfo(raidId:String) {
        common.setNowRaidInfo(raidId: raidId)
    }
    func setNowUserInfo(userName:String){
        common.setNowUserInfo(userName: userName)
    }
    
    func showRoom(roomId:String) {
        common.showRoom(roomId: roomId)
    }
    
    func setTabStatus(state:RoomState) {
        common.setTabStatus(state: state)
    }
    
    func clearFocusItem() {
        common.clearFocusItem()
    }
    
    func updateCharacter(roomId:String,userInfo:shared.UserInfo) {
        common.updateCharacter(roomId: roomId, userInfo: userInfo)
    }
    
    func getDialogAction() -> DialogAction {
        return common.dialogAction
    }
    
    func updatePartyFocusIndex(index:Int) {
        common.updatePartyFocusIndex(index: Int32(index))
    }
    
    func outOrSignOut() {
        common.outOrSignOut()
    }
    
    func setSheetSpace(space:CGFloat) {
        common.setSheetSpace(space: Float(space))
    }
    
    func deleteRoom(roomId:String) {
        common.deleteRoom(roomId: roomId)
    }
    
    func hideRoomInfo() {
        common.hideRoom()
    }

    func checkByScheme(roomId :String) {
        common.checkByScheme(roomId: roomId)
    }
}
