//
//  ShareSheer.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/29.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import AlertToast

struct ShareSheet: View {
    let roomInfo : RoomInfo
    @State private var alertCopy = false

    @State private var errMsg :String = ""

    private let imageSize : CGFloat = 40
    var body: some View {
        BaseSheet(
            title: "공유하기",
            text: nil,
            errorMsg: $errMsg
        ) {
            VStack {
                HStack{
                    Image(resource: \.ic_share_link)
                        .resizable()
                        .frame(width: imageSize, height: imageSize)
                        .onTapGesture {
                            copyToClipboard()
                        }
                    Image(resource: \.ic_share_kakaotalk)
                        .resizable()
                        .frame(width: imageSize, height: imageSize)
                        .onTapGesture {
                            copyToKaKaoTalk()
                        }
                    Spacer()
                }
                .frame(maxWidth: .infinity)
            }.padding(EdgeInsets(top: 20, leading: 10, bottom: 20, trailing: 10))
                .toast(isPresenting: $alertCopy) {
                    AlertToast(type: .regular ,title: "클립보드에 복사되었습니다.")
                }
        }
    }
    
    func copyToClipboard() {
        alertCopy = true
        UIPasteboard.general.string = roomInfo.uniqueId    }
    
    func copyToKaKaoTalk() {

    }
}
