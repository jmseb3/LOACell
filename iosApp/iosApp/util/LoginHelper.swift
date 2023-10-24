//
//  LoginHelper.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/09/21.
//  Copyright © 2023 orgName. All rights reserved.
//

import FirebaseCore
import FirebaseAuth
import GoogleSignIn
import shared

class LoginHelper {
    static let instance : LoginHelper = LoginHelper()
    
    let firebaseAuth = Auth.auth()

    func requestGoogleLogin(
        commonAction: @escaping () -> Void = {},
        successAction: @escaping () -> Void = {},
        failAction: @escaping () -> Void = {}
    ) {
        guard let presentingViewController = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first?.rootViewController else {return}
//        
        guard let clientID = FirebaseApp.app()?.options.clientID else { return }
        
        // Create Google Sign In configuration object.
        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config
        
        GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { [unowned self] result, error in
            commonAction()
            if(error != nil) {
                failAction()
                return
            }
            
            guard let user = result?.user,
                  let idToken = user.idToken?.tokenString
            else {
                failAction()
                return
            }
            
            let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: user.accessToken.tokenString)
            
            firebaseAuth.signIn(with: credential) { result, error in
                if(error == nil) {
                    successAction()
                } else {
                    failAction()
                }
            }
        }
    }
    
    func requestAnonymousLogin() {
//        firebaseAuth.signInAnonymously { authResult, error in
//            if (error == nil) {
//                self.updateDisplayName(name: NameHelper().makeName())
//            }
//        }
    }
    
    func updateDisplayName(name: String) {
//        let cr = firebaseAuth.currentUser?.createProfileChangeRequest()
//        cr?.displayName = name
//        cr?.commitChanges()
    }
    
    func signOut() {
//        do {
//            try firebaseAuth.signOut()
//        } catch let signOutError as NSError {
//            print("Error signing out: %@", signOutError)
//        }
    }
}
