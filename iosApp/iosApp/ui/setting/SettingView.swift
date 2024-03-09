//
//  SettingView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/17.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine

struct SettingView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    @State private var showSlider :Bool = false
    @Environment(\.openURL) private var openURL
    
    private var defaultValue : Binding<CGFloat> {
        Binding {
            return viewModel.defaultSpace
        } set: { value in
            viewModel.setSheetSpace(space: value)
        }
    }
    
    private var baseUrl : Binding<String> {
        Binding {
            return viewModel.baseUrl
        } set: { value in
            viewModel.setBaseUrl(url: value)
        }
    }
    
    var body: some View {
        VStack {
            LoginInfoView()
                .padding(10)
            List {
                Section("앱 설정") {
                    Button {
                        showSlider = !showSlider
                    } label: {
                        Text("시트 하단 여백 크기 조정")
                    }
                    .foregroundColor(.black)
                    if (showSlider) {
                        VStack{
                            Slider(value: defaultValue, in: 0...40, step: 1)
                            HStack() {
                                Text("하단 여백 크기 : \(Int(defaultValue.wrappedValue))")
                                Spacer()
                                RoundCornerButton(text: "테스트") {
                                    viewModel.showDialog(dialogStatus: DialogStatus.testSheet)
                                }
                            }
                        }
                    }
                    Picker("검색 사이트 변경", selection: baseUrl) {
                        ForEach(0 ..< SettingKt.UrlAddressList.count) {
                            Text(SettingKt.UrlNameList[$0]).tag(SettingKt.UrlAddressList[$0])
                        }
                    }
                    Button {
                        if let link = URL(string: "https://discord.gg/acD6rQ9Tja") {
                            openURL(link)
                        }
                    } label: {
                        Text("버그 제보 및 건의하기")
                    }
                    .foregroundColor(.black)
                }
                Section("앱 정보") {
                    LabeledContent("앱 버전", value: "\(Bundle.main.releaseVersionNumber!)(\(Bundle.main.buildVersionNumber!))")
                }
            }
            .scrollDisabled(true)
            .scrollContentBackground(.hidden)
        }
    }
}

