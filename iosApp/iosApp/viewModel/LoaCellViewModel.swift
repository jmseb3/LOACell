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
    @Published var text : String = "Loading..."
    @Published var user : User? = nil
    
    
    @Published var syncData :Bool = false
    
    @Published var roomId : String = ""
    @Published var tabState : RoomState = RoomState.raid
    
    @Published var snackBarTitle : String = ""
    
    
    let firebaseAuth = Auth.auth()
    
    let config = Config()
    
    let db = AppDataBase(driverFactory: DriverFactory())
    
    init() {
        firebaseAuth.addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
            print(">>>>>>>>>","get User Info", self.user?.uid as Any)
        }
    }
    
    func showSnackBar(
        message: String
    ) {
        self.snackBarTitle = message
    }
    
    func resetSnackBar() {
        self.snackBarTitle = ""
    }
}

