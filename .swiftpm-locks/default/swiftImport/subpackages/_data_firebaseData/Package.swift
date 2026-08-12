// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_data_firebaseData",
  platforms: [
    .iOS("16.0")
  ],
  products: [
    .library(
      name: "_data_firebaseData",
      type: .none,
      targets: ["_data_firebaseData"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      exact: "12.16.0"
    )
  ],
  targets: [
    .target(
      name: "_data_firebaseData",
      dependencies: [
        .product(
          name: "FirebaseFirestore",
          package: "firebase-ios-sdk"
        )
      ]
    )
  ]
)
