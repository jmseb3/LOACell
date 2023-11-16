//
//  RoomDialog.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/11/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RoomActionDialog: View {
    let dismiss : () -> Void
    let confirm : (_ status:Int32) -> Void
    
    var body: some View {
        BaseDialogNoButton(title: "작업을 선택해 주세요.") {
            HStack{
                RoomEnterButton(
                    resource: \.room_enter,
                    text: "입장하기"
                ){
                    confirm(1)
                }
                
                RoomEnterButton(
                    resource: \.room_make,
                    text: "방만들기"
                ){
                    confirm(2)
                }
            }
        }
    }
}

struct RoomEnterButton :View {
    let resource : KeyPath<SharedRes.images, shared.ImageResource>
    let text :String
    let confirm : () -> Void
    
    var body: some View {
        Button {
            confirm()
        } label: {
            VStack{
                Image(resource: resource)
                Text(text)
                    .foregroundColor(.black)
            }
            .frame(width: 100,height: 100)
            .overlay {
                RoundedRectangle(cornerRadius: 25)
                    .stroke(.black,lineWidth: 2)
            }
            
        }
        .padding()
        .cornerRadius(25)
    }
}
