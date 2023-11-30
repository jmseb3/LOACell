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

import KakaoSDKShare
import KakaoSDKTemplate
import KakaoSDKCommon

import SafariServices

struct ShareSheet: View {
    let roomInfo : RoomInfo
    @State private var alertCopy = false
    
    @State private var errMsg :String = ""
    @State private var sfUrl :URL? = nil
    
    private var showSheet  :Binding<Bool> {
        Binding(get: {sfUrl != nil}) { v, t in
            
        }
    }
    
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
            }
            .padding(EdgeInsets(top: 20, leading: 10, bottom: 0, trailing: 10))
            .toast(isPresenting: $alertCopy) {
                AlertToast(type: .regular ,title: "클립보드에 복사되었습니다.")
            }
            .sheet(isPresented: showSheet) {
                SafariView(url: sfUrl!)
                    .ignoresSafeArea()
            }
        }
    }
    
    func copyToClipboard() {
        alertCopy = true
        UIPasteboard.general.string = roomInfo.uniqueId
    }
    
    func copyToKaKaoTalk() {
        let title = "LoaCell의 \(roomInfo.title)방으로 초대합니다."
        
        let templatable = TextTemplate(
            text: title,
            link: Link(webUrl: nil, mobileWebUrl: nil, androidExecutionParams: ["uniqueId" : roomInfo.uniqueId], iosExecutionParams: ["uniqueId" : roomInfo.uniqueId])
        )
        // 카카오톡 설치여부 확인
        if ShareApi.isKakaoTalkSharingAvailable() {
            // 카카오톡으로 카카오톡 공유 가능
            // templatable은 메시지 만들기 항목 참고
            ShareApi.shared.shareDefault(templatable: templatable) {(sharingResult, error) in
                if let error = error {
                    print(error)
                }
                else {
                    print("shareDefault() success.")
                    
                    if let sharingResult = sharingResult {
                        UIApplication.shared.open(sharingResult.url,
                                                  options: [:], completionHandler: nil)
                    }
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용 권장
            // Custom WebView 또는 디폴트 브라우져 사용 가능
            // 웹 공유 예시 코드
            if let url = ShareApi.shared.makeDefaultUrl(templatable: templatable) {
                sfUrl = url
            }
        }
    }
}

struct SafariView: UIViewControllerRepresentable {
    
    let url: URL
    
    func makeUIViewController(context: UIViewControllerRepresentableContext<SafariView>) -> SFSafariViewController {
        return SFSafariViewController(url: url)
    }
    
    func updateUIViewController(_ uiViewController: SFSafariViewController, context: UIViewControllerRepresentableContext<SafariView>) {
        
    }
    
}
