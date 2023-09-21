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

class LoaCellViewModel: ObservableObject, DialogStatus {
    @Published var text : String = "Loading..."
    @Published var user : User? = nil
    @Published var syncData :Bool = false
    @Published var roomId : String = ""
    @Published var tabState : RoomState = RoomState.raid
    
    @Published var snackBarTitle : String = ""
    
    lazy var config :Config = Config()
    lazy var db : AppDataBase = AppDataBase(driverFactory: DriverFactory())
    lazy var  commonViewModel : CommonViewModel = CommonViewModel(coroutineScope: nil, dataBase: db, config: config, dialogStatus: self)
    init() {
        Auth.auth().addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
            print(">>>>>>>>>","get User Info", self.user?.uid as Any)
        }
    }
    
    func syncStart(force:Bool) {
        guard let userUid = user?.uid else {
            return
        }
        commonViewModel.syncStart(uid: userUid , force:force)
    }
    
    func resetSnackBar() {
        self.snackBarTitle = ""
    }
    
    func showRoomEnterError() {
        
    }
    
    func showSnackBar(msg: String) {
        
    }
}
