//
//  BaseSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct BaseSheet<Content: View>: View {
    var title :String? = nil
    var text :String = "추가"
    var action :() -> Void
    
    var enabled : Bool
    @Binding var errorMsg :String
    func clearErrorMsg() {
        errorMsg = ""
    }
    let content: () -> Content
    
    @State private var space :CGFloat = 20.0
    
    var body: some View {
        VStack {
            Spacer()
            VStack {
                if title != nil {
                    Text(title!)
                        .font(.title3)
                    Divider()
                }
                Spacer().frame(height: 20)
                content()
                Spacer().frame(height: 20)
                if !errorMsg.isEmpty {
                    Text(errorMsg)
                        .foregroundColor(.red)
                        .animation(.spring, value: errorMsg)
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                clearErrorMsg()
                            }
                        }
                }
                Button(action: {
                    action()
                }, label: {
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
                Spacer()
                    .frame(height: space)
            }
            .padding()
            .background(.white)
            .frame(alignment: .bottom)
            .cornerRadius(20, corners: [.topLeft,.topRight])
            .shadow(radius: 20)
        }.onAppear {
            Config().getFloatFlow(key: ConfigKeys().SheetSpace, defaultValue: 20.0).collect { value in
                space = value as! CGFloat
            }
        }
    }
}
