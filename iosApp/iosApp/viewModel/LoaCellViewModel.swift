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

class LoaCellViewModel: ObservableObject, DialogStatus {
    @Published var user : User? = nil
    
    @Published var logginIn :Bool = false
    @Published var syncData : Bool = false
    @Published var roomList : [RoomInfo] = []
    
    let sc : SnackbarController = SnackbarController()
    
    lazy var config :Config = Config()
    lazy var db : AppDataBase = AppDataBase(driverFactory: DriverFactory())
    lazy var commonViewModel : CommonViewModel = CommonViewModel(coroutineScope: nil, dataBase: db, config: config, dialogStatus: self)

    init() {
        Auth.auth().addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
        }
        commonViewModel.syncData.collect { value in
            self.syncData = value!.boolValue
        }
        commonViewModel.roomList.collect { value in
            self.roomList = value as! [RoomInfo]
            self.roomList.forEach { v in
                print(">>??>>>>",v)
            }
        }
    }
    
    func syncStart(force:Bool) {
        guard let userUid = user?.uid else {
            return
        }
        commonViewModel.syncStart(uid: userUid , force:force)
    }
    

    func showRoomEnterError() {
        
    }
    
    func showSnackBar(msg: String) {
        self.sc.showSnackBar(message: msg)
    }
    
    func resetSnackBar() {
        self.sc.resetSnackBar()
    }
}
