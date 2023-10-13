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
    let action : () -> Void
    let iconSize :CGFloat = 30
    
    var body: some View {
        Button {
            withAnimation {
                action()
            }
        } label: {
            Image(resource: resource)
                .resizable()
                .foregroundColor(.black)
                .frame(width: iconSize, height: iconSize)
        }
    }
}

struct IconSystemButton: View {
    let resource : String
    let action : () -> Void
    let iconSize :CGFloat = 30
    
    var body: some View {
        Button {
            action()
        } label: {
            Image(systemName: resource)
                .resizable()
                .foregroundColor(.black)
                .frame(width: iconSize, height: iconSize)
        }
    }
}

#Preview {
    IconButton(resource: \.room_make) {
        
    }
}
