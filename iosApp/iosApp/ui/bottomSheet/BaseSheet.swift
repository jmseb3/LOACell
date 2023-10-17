//
//  BaseSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI


struct BaseSheet<Content: View>: View {
    var title :String? = nil
    var text :String = "추가"
    var action :() -> Void
    
    @Binding var enabled : Bool
    @Binding var errorMsg :String
    func clearErrorMsg() {
        errorMsg = ""
    }
    let content: () -> Content
    
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
            }
            .padding()
            .background(.white)
            .frame(alignment: .bottom)
            .cornerRadius(20, corners: [.topLeft,.topRight])
            .shadow(radius: 20)
        }
    }
}
