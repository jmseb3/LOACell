//
//  DropDownCharacterName.swift
//  iosApp
//
//  Created by 정원희 on 2/24/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DropDownCharacterNameView: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    
    let name : String
    
    @State private var openSafari :Bool = false
    var body: some View {
        Menu(name) {
            Button(action: {
                openSafari = true
            }, label: {
                Label(
                    title: {
                        Text("검색")
                    },
                    icon: {
                        Image(resource: \.search)
                            .resizable()
                            .frame(width: 15,height: 15)
                    }
                )
            })
        }.foregroundColor(.black)
            .fullScreenCover(isPresented: $openSafari) {
                SafariWebView(url: SearchHelper_iosKt.makeUrl(base: viewModel.baseUrl ,name: name))
                    .ignoresSafeArea()
            }
    }
}
