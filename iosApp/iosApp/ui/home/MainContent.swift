//
//  MainContent.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/07/07.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainContent: View {
    @ObservedObject private(set) var viewModel: LoaCellViewModel
    @State var roomList : [RoomInfo] = []
    
    var body: some View {
        VStack {
            ForEach(roomList,id:\.uniqueId) { room in
                VStack{
                    Text(room.title)
                    Text(room.description_)
                    Text(room.uniqueId)
                }
            }
        }.onAppear {
            Task {
                await abdd()
            }
        }
    }
    
    func abdd() async {
        self.viewModel.db.roomInfoQueriesHelper.getAllCommonFlow().watch(block: { value in
            let a = value as! [RoomInfo]
            roomList = a
        })
        
    }
}


struct MainContent_Previews: PreviewProvider {
    static var previews: some View {
        MainContent(viewModel: LoaCellViewModel())
    }
}
