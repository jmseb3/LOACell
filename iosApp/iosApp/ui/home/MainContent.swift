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
    
    @State var text :String = "empty"
    var body: some View {
        VStack {
            Text(text)
            Button("test", action: {
                text = viewModel.user?.displayName ?? "empty"
            })
            ForEach(roomList,id:\.uniqueId) { room in
                VStack{
                    Text(room.title)
                    Text(room.description_)
                    Text(room.uniqueId)
                }
            }
        }.onAppear {
        }
    }

}


struct MainContent_Previews: PreviewProvider {
    static var previews: some View {
        MainContent(viewModel: LoaCellViewModel())
    }
}
