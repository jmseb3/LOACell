//
//  LoadingView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/09/25.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI

struct LoadingView: View {
    var info : String = ""
    var color : Color = .gray
    var body: some View {
        ZStack {
            color
                .opacity(0.5)
                .onTapGesture {
                    
                }
                .ignoresSafeArea()
            
            ProgressView(label: {
                Text(info)
            }).frame(maxWidth: .infinity,maxHeight: .infinity,alignment: .center)
        }
       
    }
}

#Preview {
    LoadingView(info: "동기화 중입니다.")
}
