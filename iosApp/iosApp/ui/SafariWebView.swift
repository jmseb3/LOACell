//
//  SafariWebView.swift
//  iosApp
//
//  Created by 정원희 on 2/24/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import SafariServices

struct SafariWebView: UIViewControllerRepresentable {
    let url: URL
    
    func makeUIViewController(context: Context) -> SFSafariViewController {
        return SFSafariViewController(url: url)
    }
    
    func updateUIViewController(_ uiViewController: SFSafariViewController, context: Context) {
        
    }
}
