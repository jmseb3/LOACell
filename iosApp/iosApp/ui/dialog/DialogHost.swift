//
//  DialogHost.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/16.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct DialogHost<Content: View>: View {
    @EnvironmentObject var viewModel: LoaCellViewModel
    private let content: () -> Content
    
    public init(
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.content = content
    }
    
    func dismiss() {
        viewModel.commonViewModel.hideAllDialog()
    }
    
    var body: some View {
        ZStack {
            GeometryReader {g in
                ZStack {
                    Color(.white)
                    VStack {
                        content()
                    }
                }
                
                if viewModel.dialogStatus != DialogStatus.none {
                    ZStack {
                        Color(.black)
                            .opacity(0.5)
                            .onTapGesture {
                                dismiss()
                            }
                            .ignoresSafeArea()
                        VStack(alignment: .center) {
                            switch viewModel.dialogStatus {
                            case .characterEdit:
                                EditCharacterDialog(
                                    characterList: viewModel.characterList,
                                    representativeCharacter: viewModel.userInfo?.representativeCharacter ?? "",
                                    leftAction: {
                                        dismiss()
                                    },
                                    rightAction: {name in
                                        CommonUserHelper()
                                            .updateRepresentativeCharacter(
                                                roomId: viewModel.roomId,
                                                name: viewModel.userInfo!.name,
                                                representativeCharacter: name
                                            )
                                        dismiss()
                                    })
                            case .characterDelete:
                                if viewModel.userInfo != nil {
                                    let name = viewModel.userInfo!.name
                                    DeleteCharacterDialog(name: name) {
                                        CommonUserHelper().delete(
                                            roomId: viewModel.roomId,
                                            name: name
                                        ) { error in
                                            viewModel.showSnackBar(msg: error)
                                        } successAction: {
                                            viewModel.commonViewModel.clearFocusItem()
                                            dismiss()
                                        }
                                    } dismiss: {
                                        dismiss()
                                    }
                                }
                            default:
                                Text("\(viewModel.dialogStatus.name)")
                            }
                        }
                        .padding()
                        .background(.white)
                        .cornerRadius(20)
                        .frame(width: g.size.width * 0.8)
                        .position(x: g.size.width / 2, y: g.size.height / 2)
                        .shadow(radius: 20)
                    }
                    .animation(.easeInOut, value: viewModel.dialogStatus)
                }
            }
        }
    }
}
