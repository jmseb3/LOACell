//
//  LengthLimitTextField.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/18.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import Combine

struct LengthLimitTextField: View {
    let maxLength : Int
    let placeHolder : String
    @Binding var text :String {
        didSet {
            if text.count > maxLength && oldValue.count <= maxLength {
                text = oldValue
            }
        }
    }
    var body: some View {
        VStack {
            Text("\(text.count)/\(maxLength)")
                .frame(maxWidth: .infinity,alignment: .trailing)
            TextField(placeHolder, text: $text)
                .padding()
                .autocapitalization(.none)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.gray,lineWidth: 2)
                )
                .onReceive(Just(text), perform: { _ in
                    limitText()
                })
        }.frame(maxWidth: .infinity)
    }
    
    private func limitText() {
        if text.count > maxLength {
            text = String(text.prefix(maxLength))
        }
    }
}
