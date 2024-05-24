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
    var text :String? = "추가"
    var action :() -> Void = {}
    var enabled : Bool = true
    
    @Binding var errorMsg :String
    func clearErrorMsg() {
        errorMsg = ""
    }
    var dismiss : () -> Void = {}
    
    let content: () -> Content
    
    @State private var space :CGFloat = 20.0
    
    var body: some View {
        NavigationView {
            VStack {
                content()
                Spacer().frame(height: 10)
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
                if let buttonText = text {
                    Spacer()
                    RoundCornerButton(text: buttonText,enabled:enabled) {
                        action()
                    }
                }
                Spacer()
                    .frame(height: space)
            }
            .navigationTitle(title ?? "")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar(content: {
                ToolbarItem {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName:"xmark")
                            .renderingMode(.template)
                            .foregroundColor(.black)
                    }
                    
                }
            })
            .padding()
            .frame(minHeight: 350)
            .onAppear {
                getSheetSpace()
            }
        }
        .presentationDetents([.medium])
    }
    
    @MainActor
    func getSheetSpace() {
        ModuleProvider().getSheetSpace { value, error in
            print("get Space : \(value)")
            if(error != nil) {
                self.space = value as! CGFloat
            }
        }
    }
}
