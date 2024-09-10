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
            window.rootViewController = MainKt.MainViewController(createUIViewController: { () -> UIViewController in
                let appleLoginButton = AppleSignInButton()
                return UIHostingController(rootView: appleLoginButton)
            })
            window.makeKeyAndVisible()
        }
        return true
    }
}
