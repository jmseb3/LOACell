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
        VStack(alignment : .center) {
            if title != nil {
                Text(title!)
                    .font(.title3)
                Divider()
            }
            Spacer().frame(height: 20)
            content()
            Spacer().frame(height: 20)
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
        .background(.white)
        .cornerRadius(20)
        .shadow(radius: 20)
    }
}

struct DeleteDialog<Content: View>: View {
    var title :String? = nil
    var leftAction : () -> Void
    var rightAction :() -> Void
    @Binding var rightEnabled : Bool
    
    let content: () -> Content
    var body: some View {
        BaseDialog(
            title: title,
            leftText: "취소",
            rightText: "삭제",
            leftAction: leftAction,
            rightAction: rightAction,
            rightEnabled: $rightEnabled
        ) {
            content()
        }
    }
}
