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
    
    var heightFactor: CGFloat {
        UIScreen.main.bounds.height > 800 ? 3.6 : 3
    }
    
    func dismiss() {
        viewModel.commonViewModel.hideAllDialog()
    }
    
    var body: some View {
        ZStack {
            GeometryReader {gemetryReader in
                let dialog = Dialog(gemetryReader: gemetryReader)
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
                        if viewModel.dialogStatus == DialogStatus.characterEdit {
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
                            .modifier(dialog)
                        } else if (viewModel.dialogStatus == DialogStatus.characterDelete) {
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
                                .modifier(dialog)

                            }
                        } else if viewModel.dialogStatus == DialogStatus.userAdd {
                            AddUserSheet(
                                roomId : viewModel.roomId
                            ) {
                                dismiss()
                            }

                        } else if viewModel.dialogStatus == DialogStatus.raidAdd {
                            AddRaidSheet(
                                roomId : viewModel.roomId
                            ) {
                                dismiss()
                            }
                        }
                        
                    }
                    .animation(.spring, value: viewModel.dialogStatus)
                }
            }
        }
    }
}


struct Dialog: ViewModifier {
    let gemetryReader : GeometryProxy
    func body(content: Content) -> some View {
        content
            .padding()
            .frame(width: gemetryReader.size.width * 0.8 , alignment: .center)
            .position(x: gemetryReader.size.width / 2, y : gemetryReader.size.height / 2)

    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape( RoundedCorner(radius: radius, corners: corners) )
    }
}

struct RoundedCorner: Shape {
    
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners
    
    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
