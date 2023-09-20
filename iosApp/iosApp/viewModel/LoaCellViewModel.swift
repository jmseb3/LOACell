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

class LoaCellViewModel: ObservableObject {
    
    class DT :DialogStatus {
        func showRoomEnterError() {
            
        }
        
        func showSnackBar(msg: String) {
            
        }
    }

    @Published var text : String = "Loading..."
    @Published var user : User? = nil
    @Published var syncData :Bool = false
    @Published var roomId : String = ""
    @Published var tabState : RoomState = RoomState.raid
    
    @Published var snackBarTitle : String = ""
    
    
    let firebaseAuth = Auth.auth()
    let config :Config
    let db : AppDataBase
    let commonViewModel : CommonViewModel
    let dt :DialogStatus
    init() {
        self.config = Config()
        self.db = AppDataBase(driverFactory: DriverFactory())
        self.dt = DT()
        self.commonViewModel = CommonViewModel(coroutineScope: nil, dataBase: db, config: config, dialogStatus: dt)
        firebaseAuth.addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
            print(">>>>>>>>>","get User Info", self.user?.uid as Any)
        }
    }
    
    func syncStart(force:Bool) {
        commonViewModel.syncStart(uid: user!.uid , force:force)
    }
    
    func resetSnackBar() {
        self.snackBarTitle = ""
    }
}
