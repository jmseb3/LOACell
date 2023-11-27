//
//  SectionCardView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
struct SectionCardView<Content: View>: View {
    var title : String? = nil
    var iconSrc : KeyPath<SharedRes.images,shared.ImageResource>? = nil
    var iconAction : () -> Void = {}
    let content: () -> Content
    
    var body: some View {
        VStack {
            ZStack {
                if let title = title {
                    Text(title)
                        .frame(maxWidth: .infinity,alignment: .leading)
                        .font(.system(size: 20))
                }
                if let icon = iconSrc {
                    IconButton(resource: icon) {
                        iconAction()
                    }
                    .frame(alignment: .trailing)
                }
            }
            if (title != nil || iconSrc != nil) {
                Divider()
            }
            content()
        }
        .frame(maxWidth: .infinity)
        .padding(10)
        .background(ColorManager.WheelBorder)
        .cornerRadius(20)
        .overlay {
            RoundedRectangle(cornerRadius: 20)
                .stroke(.black,lineWidth: 1)
        }
    }
}
