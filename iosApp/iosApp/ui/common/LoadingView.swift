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
    var body: some View {
        ProgressView(label: {
            Text(info)
        })
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView(info: "동기화 중입니다.")
    }
}
