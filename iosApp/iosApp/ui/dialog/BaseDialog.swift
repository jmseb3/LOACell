//
//  BaseDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct BaseDialog<Content: View>: View {
    var title :String? = nil
    
    var leftText :String = "취소"
    var rightText :String = "확인"
    
    var leftAction : () -> Void
    var rightAction :() -> Void
    
    @Binding var rightEnabled : Bool
    
    let content: () -> Content
    
    var body: some View {
        VStack {
            if title != nil {
                Text(title!)
                    .font(.title2)
                Divider()
            }
            content()
            HStack {
                Spacer()
                Button(action: {
                    leftAction()
                }, label: {
                    Text(leftText)
                        .foregroundColor(.black)
                })
                Spacer().frame(width: 20)
                Button(action: {
                    rightAction()
                }, label: {
                    Text(rightText)
                        .foregroundColor(rightEnabled ? .black : .gray)
                })
                .disabled(!rightEnabled)
            }
        }
        .padding()
        .frame(alignment: .center)
        .clipShape(RoundedRectangle(cornerRadius: 20))
    }
}
