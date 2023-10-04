//
//  RoomListView.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/04.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RoomListView: View {
    let roomList : [RoomInfo]
    let action :(_ roomId:String) -> Void
    
    var body: some View {
        LazyVStack(spacing : 10, content: {
            ForEach(roomList, id: \.uniqueId) { room in
                VStack {
                    RoomInfoRow(room: room) {
                        action(room.uniqueId)
                    }
                    Spacer()
                        .frame(height: 10)
                }
                
            }
        }).padding(EdgeInsets(top: 0, leading: 10, bottom: 0, trailing: 10))
    }
}

struct RoomInfoRow: View {
    let room : RoomInfo
    let action :() -> Void
    
    var body: some View {
        VStack {
            Text(room.title)
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.system(size: 18))
                .lineLimit(1)
            
            Text(room.description_)
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.system(size: 14))
                .lineLimit(2)
        }
        .padding(EdgeInsets(top: 5, leading: 5, bottom: 5, trailing: 5))
        .clipShape(RoundedRectangle(cornerRadius : 10))
        .background(Color.gray)
    }
}


