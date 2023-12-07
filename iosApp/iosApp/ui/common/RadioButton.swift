//
//  RadioButton.swift
//  iosApp
//
//  Created by WonHee Jung on 12/5/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct RadioButton :View {
    let selected : Bool
    let text :String
    let enabled :Bool
    let action : () -> Void
    var body: some View {
        HStack {
            Button {
                action()
            } label: {
                HStack{
                    if selected {
                        ZStack{
                            Circle()
                                .fill(Color.white)
                                .frame(width: 20, height: 20)
                                .overlay(Circle().stroke(Color.blue, lineWidth: 1))
                            Circle()
                                .fill(Color.blue)
                                .frame(width: 8, height: 8)
                        }
                    } else {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 20, height: 20)
                            .overlay(Circle().stroke(Color.gray, lineWidth: 1))
                    }
                    Text(text)
                        .frame(alignment: .center)
                        .frame(maxWidth: .infinity)
                        .foregroundColor(enabled ? .black : .gray)
                }
            }.disabled(!enabled)
        }
    }
}
