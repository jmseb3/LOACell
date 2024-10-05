import UIKit
import SwiftUI
import ComposeApp
import FirebaseCore

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        FirebaseApp.configure()
        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController(
                appleSingIn: { () -> UIViewController in
                    let appleLoginButton = AppleSignInButton()
                    return UIHostingController(rootView: appleLoginButton)
                },
                appleLoginGuide: AppleGuide()
            )
            window.makeKeyAndVisible()
        }
        return true
    }
}

class AppleGuide : AppleLoginGuide {
    func linkToApple(fail: @escaping (String) -> Void, success: @escaping () -> Void) {
        print("link To Apple!!!")
        AppleLinkCoordinator(linkFailAction: fail, linkSuccessAction: success).startLogin()
    }
    
    func revokeToken(fail: @escaping (String) -> Void, success: @escaping () -> Void) {
        print("revoke To Apple!!!")
        AppleTokenRevokeCoordinator(failAction: fail, revokeSuccessAction: success).startLogin()
    }
}
