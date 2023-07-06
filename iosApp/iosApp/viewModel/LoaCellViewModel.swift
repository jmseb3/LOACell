//
//  LoaCellViewModel.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/06.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth

class LoaCellViewModel: ObservableObject {
    @Published var text : String = "Loading..."
    @Published var user : User? = nil
    
    let firebaseAuth = Auth.auth()

    init() {
        firebaseAuth.addStateDidChangeListener { auth, getUser in
            self.user = auth.currentUser
            print(">>>>>>>>>","get User Info", self.user?.uid)
        }
    }
    
}

