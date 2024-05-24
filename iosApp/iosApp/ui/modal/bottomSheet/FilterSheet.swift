//
//  FilterSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 12/4/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct FilterSheet: View {
    private let totalRoomInfo : TotalRoomInfo
    private let updateFilter : (_ filter : Filter) -> Void
    private let dismiss : () -> Void
    
    
    private var filter : Filter {
        totalRoomInfo.filter
    }
    private var userInfoList : [UserInfo] {
        totalRoomInfo.userInfoList
    }
    private let raidTypes : [RaidType] = RaidType.entries
    
    private var showRow : Binding<Bool> {
        Binding {
            filter.showEmptyCalendarRow
        } set: { v, t in
            updateFilter(filter.updateEmptyCalendarRow(show: !filter.showEmptyCalendarRow))
        }
    }
    
    init(dialogAction : DialogAction) {
        self.totalRoomInfo = dialogAction.getTotalRoomInfo()
        self.updateFilter = {filter in
            dialogAction.dialogFilterUpdate(filter: filter)
        }
        self.dismiss = {
            dialogAction.hideDialog()
        }
    }
    
    var body: some View {
        BaseSheet(
            title: Sheet.raidFilter.title,
            text: nil,
            errorMsg: .constant(""),
            dismiss: dismiss
    
        ) {
            VStack{
                FilterSection(section: "레이드 종류") {
                    LazyVGrid(
                        columns: [
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                            GridItem(.flexible(), spacing: 3, alignment: nil)
                        ],
                        spacing: 6
                    ) {
                        ForEach(raidTypes,id: \.name) { raid in
                            Chips(title: raid.toKorString(),isSelected: filter.isSelected(type: raid)) {
                                updateFilter(filter.updateRaidType(type: raid))
                            }
                        }
                    }
                    .padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))

                }
                FilterSection(section: "완료 여부") {
                    HStack{
                        ForEach(Filter.FINISH.entries,id:\.name) { finish in
                            RadioButton(
                                selected: filter.isSelected(finish: finish),
                                text: finish.title,
                                enabled: true
                            ) {
                                updateFilter(filter.updateFinish(finish: finish))
                            }
                        }
                    }
                    .padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))
                }
                FilterSection(section: "특정 유저 포함") {
                    LazyVGrid(
                        columns: [
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                            GridItem(.flexible(), spacing: 3, alignment: nil),
                        ],
                        spacing: 6
                    ) {
                        ForEach(userInfoList,id: \.name) { userInfo in
                            let name = userInfo.name
                            Chips(
                                title: name,
                                isSelected: filter.isSelected(user: name)
                            ) {
                                updateFilter(filter.updateUser(user: name))
                            }
                        }
                    }
                    .padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))

                }
                FilterSection(section: "캘린더 조절") {
                    VStack {
                        HStack {
                            ForEach([60,30,15],id: \.self) { time in
                                Button {
                                    updateFilter(filter.updateTimeStep(step: Int32(time)))
                                } label: {
                                    Text("\(time)분")
                                        .frame(maxWidth: .infinity)
                                }
                                
                            }
                        }
                        HStack {
                            Toggle(
                                isOn: showRow,
                                label: {
                                    Text("빈 행 보이기")
                                }
                            )
                        }
                    }
                }
                HStack {
                    Spacer()
                    Button(action: {
                        updateFilter(filter.clear())
                    }, label: {
                        Text("필터 초기화")
                    })
                }
            }
        }
    }
}

struct FilterSection<Content: View>: View {
    let section :String
    let content: () -> Content
    @State private var show :Bool = false
    
    var body: some View {
        VStack {
            HStack {
                Text(section)
                Spacer()
                Image(resource: \.arrow)
                    .resizable()
                    .frame(width: 18,height: 18)
                    .rotationEffect(show ? Angle(degrees: 90.0) : .zero)
            }
            .padding(EdgeInsets(top: 3, leading: 10, bottom: 3, trailing: 10))
            .onTapGesture {
                show = !show
            }
            if show {
                content()
            }
        }
        .frame(maxWidth: .infinity)
        .padding(10)
        .cornerRadius(20)
        .overlay {
            RoundedRectangle(cornerRadius: 20)
                .stroke(.black,lineWidth: 1)
        }
    }
}

struct Chips: View {
    let title : String
    let isSelected: Bool
    let updateClick :() -> Void
    var body: some View {
        HStack {
            Text(title)
                .font(.title3)
                .lineLimit(1)
        }
        .padding(EdgeInsets(top: 2, leading: 5, bottom: 2, trailing: 5))
        .frame(maxWidth: .infinity)
        .foregroundColor(isSelected ? .white : .blue)
        .background(isSelected ? Color.blue : Color.white) //different UI for selected and not selected view
        .cornerRadius(40)  //rounded Corner
        .overlay(
            RoundedRectangle(cornerRadius: 40)
                .stroke(Color.blue, lineWidth: 1.5)
        ).onTapGesture {
            updateClick()
        }
    }
}
