//
//  SettingView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SettingView: View {
    @State private var showSlider :Bool = false
    var body: some View {
        VStack {
            SectionCardView(title:"로그인 정보") {
                VStack {
                    LoginInfoView()
                }
            }
            SectionTextWithContent(
                title: "시트 하단 여백 크기 조정",
                useDivider: true,
                clikced: $showSlider
            ) {
                VStack{
                    Text("hihi")
                }
            }
            SectionText(title: "버그 제보 및 건의하기") {
                if let link = URL(string: "https://discord.gg/acD6rQ9Tja") {
                    UIApplication.shared.open(link)
                }
            }
            SectionText(title: "앱 버전 : \(Bundle.main.releaseVersionNumber!)(\(Bundle.main.buildVersionNumber!))")
        }
        .padding(10)
    }
}

struct SectionTextWithContent<Content: View> : View {
    let title :String
    var useDivider :Bool = true
    @Binding var clikced : Bool
    let content : () -> Content
    var body: some View {
        VStack{
            HStack {
                Text(title)
                Spacer()
                Image(resource: \.arrow)
                    .resizable()
                    .frame(width: 12,height: 12)
                    .rotationEffect(clikced ? .degrees(90) : .zero)
            }
            .frame(maxWidth: .infinity)
            .onTapGesture {
                clikced = !clikced
            }
            if clikced{
                content()
            }
            if useDivider {
                Divider()
            }
        }
        .frame(maxWidth: .infinity)
        .padding(10)
    }
}

struct SectionText : View {
    let title :String
    var useDivider :Bool = true
    var action : (() -> Void)? = nil
    var body: some View {
        VStack{
            HStack {
                Text(title)
                Spacer()
                if action != nil {
                    Image(resource: \.arrow)
                        .resizable()
                        .frame(width: 12,height: 12)
                }
            }
            .frame(maxWidth: .infinity)
            .onTapGesture {
                action?()
            }
            if useDivider {
                Divider()
            }
        }
        .frame(maxWidth: .infinity)
        .padding(10)
    }
}
