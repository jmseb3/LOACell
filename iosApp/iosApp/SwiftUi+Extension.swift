//
//  SwiftUi+Extension.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/12.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import SwiftUI

extension Image {
    init(resource: KeyPath<SharedRes.images, shared.ImageResource>) {
        self.init(uiImage: SharedRes.images()[keyPath: resource].toUIImage()!)
    }
}

extension String {
    var isNotEmpty : Bool {
        return !self.isEmpty
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape( RoundedCorner(radius: radius, corners: corners) )
    }
}
