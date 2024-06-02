//
//  Tabs.swift
//  iosApp
//
//  Created by WonHee Jung on 5/16/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct Tabs: View {
    var fixed = true
    let tabs: [String]
    let geoWidth: CGFloat
    
    @Binding var selectedTab: Int
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            ScrollViewReader { proxy in
                VStack(spacing: 0) {
                    HStack(spacing: 0) {
                        ForEach(0 ..< tabs.count, id: \.self) { row in
                            Button(action: {
                                withAnimation {
                                    selectedTab = row
                                }
                            }, label: {
                                VStack(spacing: 0) {
                                    HStack {
                                        // Text
                                        Text(tabs[row])
                                            .font(Font.system(size: 18, weight: .semibold))
                                            .foregroundColor(Color.black)
                                            .padding(EdgeInsets(top: 10, leading: 3, bottom: 10, trailing: 15))
                                    }
                                    .frame(width: fixed ? (geoWidth / CGFloat(tabs.count)) : .none, height: 52)
                                    // Bar Indicator
                                    Rectangle().fill(selectedTab == row ? Color.blue : Color.clear)
                                        .frame(height: 3)
                                }.fixedSize()
                            })
                                .accentColor(Color.white)
                                .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .onChange(of: selectedTab) { target in
                        withAnimation {
                            proxy.scrollTo(target)
                        }
                    }
                }
            }
        }
        .frame(height: 55)
        .onAppear{
            UIScrollView.appearance().bounces = fixed ? false : true
        }
        .onDisappear{
            UIScrollView.appearance().bounces = true
        }
    }
}
