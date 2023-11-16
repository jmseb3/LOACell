//
//  CheckButton.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct CheckButton :View {
    let isOn :Bool
    let text :String
    let action :() -> Void
    var body: some View {
        Button(action: action){
            HStack {
                Image(systemName: isOn ? "checkmark.square" : "square")
                    .foregroundColor(isOn ? .blue :.gray)
                Text(text)
                    .foregroundColor(.black)
                Spacer()
            }
        }.frame(maxWidth: .infinity)
    }
}
