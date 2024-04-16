//
//  SwiftUi+Extension.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/12.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import SwiftUI
import Foundation

extension Image {
    init(resource: KeyPath<SharedRes.images, shared.ImageResource>) {
        self.init(uiImage: SharedRes.images()[keyPath: resource].toUIImage()!)
    }
}


extension String {
    var isNotEmpty : Bool {
        return !self.isEmpty
    }
    
    func ifEmpty(defaultValue : () -> String) -> String {
        if self != "" {
            return self
        } else {
            return defaultValue()
        }
    }
}


extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape( RoundedCorner(radius: radius, corners: corners) )
    }
}

extension Bundle {
    var releaseVersionNumber: String? {
        return infoDictionary?["CFBundleShortVersionString"] as? String
    }
    var buildVersionNumber: String? {
        return infoDictionary?["CFBundleVersion"] as? String
    }
}

extension URL {
    var queryParameters: QueryParameters {
      return QueryParameters(url: self) // 연산프로퍼티
    }
}

class QueryParameters {
    let queryItems: [URLQueryItem]
    
    init(url: URL?) {
        queryItems = URLComponents(string: url?.absoluteString ?? "")?.queryItems ?? []
        print(queryItems)
    }
    
    subscript(name: String) -> String? {
        return queryItems.first(where: { $0.name == name })?.value
    }
}

extension View {
    func snapshot() -> UIImage {
        let controller = UIHostingController(rootView: self)
        let view = controller.view

        let targetSize = controller.view.intrinsicContentSize
        view?.bounds = CGRect(origin: .zero, size: targetSize)
        view?.backgroundColor = .clear

        let renderer = UIGraphicsImageRenderer(size: targetSize)

        return renderer.image { _ in
            view?.drawHierarchy(in: controller.view.bounds, afterScreenUpdates: true)
        }
    }
}

extension Binding where Value == String {
    func max(_ limit: Int) -> Self {
        if self.wrappedValue.count > limit {
            DispatchQueue.main.async {
                self.wrappedValue = String(self.wrappedValue.prefix(limit))
            }
        }
        return self
    }
}
