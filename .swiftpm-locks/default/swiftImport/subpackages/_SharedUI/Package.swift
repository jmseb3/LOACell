// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_SharedUI",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_SharedUI",
      type: .none,
      targets: ["_SharedUI"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.16.0"
    ),
    .package(
      url: "https://github.com/google/GoogleSignIn-iOS.git",
      exact: "9.2.0"
    )
  ],
  targets: [
    .target(
      name: "_SharedUI",
      dependencies: [
        .product(
          name: "FirebaseAuth",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "FirebaseCore",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "FirebaseCrashlytics",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "FirebaseFirestore",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "FirebaseStorage",
          package: "firebase-ios-sdk"
        ),
        .product(
          name: "GoogleSignIn",
          package: "GoogleSignIn-iOS"
        )
      ]
    )
  ]
)
