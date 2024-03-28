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
    let enabled :Bool
    let action : () -> Void
    
    init(text: String, action: @escaping () -> Void) {
        self.text = text
        self.enabled = true
        self.action = action
    }
    
    init(text: String, enabled: Bool, action: @escaping () -> Void) {
        self.text = text
        self.enabled = enabled
        self.action = action
    }
    var body: some View {
        Button(action: action, label: {
            Text(text)
                .foregroundColor(enabled ? .black : .gray)
                .frame(maxWidth: .infinity)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(.black, lineWidth: 1)
                )
        })
        .disabled(!enabled)
    }
}
