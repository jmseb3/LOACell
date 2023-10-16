//
//  IconButton.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/13.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct IconButton: View {
    let resource :KeyPath<SharedRes.images, shared.ImageResource>
    var enabled :Bool = true
    let iconSize :CGFloat = 30
    let action : () -> Void
    
    var body: some View {
        Button {
            withAnimation {
                action()
            }
        } label: {
            Image(resource: resource)
                .resizable()
                .renderingMode(.template)
                .foregroundColor(enabled ? .black : .gray)
                .frame(width: iconSize, height: iconSize)
        }.disabled(!enabled)
    }
}

