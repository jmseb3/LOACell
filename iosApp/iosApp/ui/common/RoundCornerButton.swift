//
//  RoundCornerButton.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/12/04.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct RoundCornerButton: View {
    let text :String
    let action : () -> Void
    var body: some View {
        Button(action: action, label: {
            Text(text)
                .foregroundColor(.black)
                .frame(maxWidth: .infinity)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(.black, lineWidth: 1)
                )
        })
    }
}
