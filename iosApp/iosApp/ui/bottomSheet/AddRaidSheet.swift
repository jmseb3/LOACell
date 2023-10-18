//
//  AddRaidSheet.swift
//  iosApp
//
//  Created by WonHee Jung on 2023/10/18.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import Combine

struct AddRaidSheet: View {
    let roomId :String
    @State private var fbRaidInfo = FBRaidInfo(
        title: "",
        type: RaidType.valtan,
        difficulty: Difficulty.normal,
        startGateNumber: 1,
        endGateNumber: 1,
        isFinish: false,
        party1: [String](repeating: "", count: 4),
        party2: [String](repeating: "", count: 4),
        day: Day.none,
        hour: 0,
        minute: 0
    )
    let successAction :() -> Void
    var body: some View {
        RaidSheetBase(
            fbRaidInfo: $fbRaidInfo,
            title: "레이드 정보 추가",
            buttonText: "추가",
            onDismiss: {}) {
                //
                print("JWH",fbRaidInfo)
                CommonRaidHelper().add(
                    roomId: roomId,
                    fbRaidInfo: fbRaidInfo
                ) { error in
                    print(error)
                } successAction: {
                    successAction()
                }
            }
    }
}

private struct RaidSheetBase: View {
    @Binding var fbRaidInfo :FBRaidInfo
    let title :String
    let buttonText :String
    let onDismiss :() -> Void
    let buttonAction :() -> Void
    
    @State private var titleText : String = ""
    @State private var raidType : RaidType  = RaidType.etc
    @State private var abStart :Int = 1
    @State private var abEnd :Int = 1
    
    @State private var dayHour :Int = 0
    @State private var dayMin :Int = 0
    
    @State private var showGateEdit : Bool = false
    @State private var showDayUse :Bool = false
    
    var body: some View {
        BaseSheet(
            title: title,
            text: buttonText,
            action: {
                buttonAction()
            },
            enabled: !fbRaidInfo.title.isEmpty && (showDayUse ? fbRaidInfo.day != Day.none : true),
            errorMsg: .constant("")
        ) {
            VStack {
                LengthLimitTextField(
                    maxLength: 10,
                    placeHolder: "제목을 입력해주세요.",
                    text: $titleText
                )
                VStack {
                    RaidSheetHeadeerText(text: "레이드 정보 선택")
                    Divider()
                    Picker("레이드 정보 선택",selection: $raidType) {
                        ForEach(RaidType.entries, id:\.self) { item in
                            Text(item.toKorString())
                        }
                    }
                    .accentColor(.black)
                    .onAppear{
                        raidType = fbRaidInfo.type
                    }
                    
                }
                VStack{
                    RaidSheetHeadeerText(text: "난이도 선택")
                    Divider()
                    DifficultyRow(fbRaidInfo: $fbRaidInfo, difficultyList: [Difficulty.normal,Difficulty.hard,Difficulty.hell])
                    if (Const().useExtreme) {
                        DifficultyRow(fbRaidInfo: $fbRaidInfo, difficultyList: [Difficulty.extremenormal,Difficulty.extremehard])
                    }
                }
                if showGateEdit {
                    VStack{
                        RaidSheetHeadeerText(text: "관문 선택")
                        HStack {
                            ForEach(Array(1...4),id:\.self) { idx in
                                let isOn = Array(abStart...abEnd).contains(idx)
                                CheckButton(isOn: isOn, text: String(idx)) {
                                    if(isOn) {
                                        if (abStart == idx) {
                                            abStart = idx + 1
                                        } else if(abEnd == idx) {
                                            abEnd = idx - 1
                                        }
                                        if (abEnd < abStart) {
                                            abStart = 1
                                            abEnd = 1
                                        }
                                    } else {
                                        abStart = min(abStart,idx)
                                        abEnd = max(idx,abEnd)
                                    }
                                    fbRaidInfo = fbRaidInfo.updateGate(start:Int32(abStart),end:Int32(abEnd))
                                }
                            }
                        }
                    }
                    .onAppear{
                        abStart = 1
                        abEnd = 1
                    }
                    .animation(.spring, value : showGateEdit)
                }
                HStack {
                    VStack {
                        RaidSheetHeadeerText(text: "입장 레벨")
                        Text(fbRaidInfo.getMinLevelText())
                            .frame(maxWidth: .infinity)
                            .frame(alignment: .leading)
                    }
                    VStack {
                        RaidSheetHeadeerText(text: "입장 인원")
                        Text(String(fbRaidInfo.type.maxPerson))
                            .frame(maxWidth: .infinity)
                            .frame(alignment: .leading)
                    }
                }
                Spacer().frame(height: 10)
                CheckButton(isOn: showDayUse, text: "일정 지정") {
                    withAnimation {
                        showDayUse = !showDayUse
                    }
                }
                if showDayUse {
                    let h :CGFloat = 100
                    HStack {
                        ZStack(alignment: .center) {
                            HStack {
                                Picker("Choose a Hour", selection: $dayHour) {
                                    ForEach(Array(0...23), id: \.self) { hour in
                                        Text(String(format: "%02d", hour))
                                    }
                                }
                                .frame(height: h)
                                .pickerStyle(.wheel)
                                .clipped()
                                Text(":")
                                Picker("Choose a Minute", selection: $dayMin) {
                                    ForEach(Array(0...59), id: \.self) { min in
                                        Text(String(format: "%02d", min))
                                    }
                                }
                                .frame(height: h)
                                .pickerStyle(.wheel)
                                .clipped()
                            }
                            .cornerRadius(20)
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(.black, lineWidth: 1)
                            )
                        }
                        .frame(minWidth: 0, maxWidth: .infinity)
                        .frame(height: h)
                        VStack {
                            let days = Day.entries
                            HStack{
                                ForEach(days[1...4],id:\.self) { day in
                                    DayButton(day: day, selected: fbRaidInfo.day == day) {
                                        updateDay(day: day)
                                    }
                                }
                            }
                            HStack{
                                ForEach(days[5...7],id:\.self) { day in
                                    DayButton(day: day, selected: fbRaidInfo.day == day) {
                                        updateDay(day: day)
                                    }
                                }
                                DayButton(day: Day.none, selected: false) {
                                    
                                }
                            }
                        }
                        .frame(height: h)
                        .padding()
                        .frame(minWidth: 0, maxWidth: .infinity)
                    }}
                
            }
            .onChange(of: titleText) { value in
                fbRaidInfo = fbRaidInfo.updateTitle(title: value)
            }
            .onChange(of: raidType) { value in
                fbRaidInfo = fbRaidInfo.updateType(type: value)
            }
            .onChange(of: fbRaidInfo) {value in
                withAnimation {
                    self.showGateEdit = (value.type == RaidType.abrelshud) && (value.difficulty != Difficulty.hell)
                }
            }
        }
    }
    
    private func updateDay(day:Day) {
        if fbRaidInfo.day == day {
            fbRaidInfo = fbRaidInfo.updateDay(day:Day.none)
        } else {
            fbRaidInfo = fbRaidInfo.updateDay(day:day)
        }
    }
}

private struct RaidSheetHeadeerText : View {
    let text : String
    var body: some View {
        VStack {
            Spacer().frame(height: 10)
            Text(text)
                .font(.system(size: 14))
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity,alignment: .leading)
        }
    }
}

private struct DifficultyRow : View {
    @Binding var fbRaidInfo :FBRaidInfo
    let difficultyList : [Difficulty]
    var body: some View {
        HStack {
            ForEach(difficultyList,id:\.name) { difficulty in
                let selected = fbRaidInfo.difficultySelected(difficulty :difficulty)
                let enabled = fbRaidInfo.difficultyEnabled(difficulty :difficulty)
                RadioButton(
                    selected: selected,
                    text: difficulty.toKorString(),
                    enabled: enabled
                )
                {
                    fbRaidInfo = fbRaidInfo.updateDifficulty(difficulty: difficulty)
                }
                .frame(maxWidth: .infinity)
            }
            
        }.frame(maxWidth: .infinity)
    }
}

private struct RadioButton :View {
    let selected : Bool
    let text :String
    let enabled :Bool
    let action : () -> Void
    var body: some View {
        HStack {
            Button {
                action()
            } label: {
                HStack{
                    if selected {
                        ZStack{
                            Circle()
                                .fill(Color.white)
                                .frame(width: 20, height: 20)
                                .overlay(Circle().stroke(Color.blue, lineWidth: 1))
                            Circle()
                                .fill(Color.blue)
                                .frame(width: 8, height: 8)
                        }
                    } else {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 20, height: 20)
                            .overlay(Circle().stroke(Color.gray, lineWidth: 1))
                    }
                    Text(text)
                        .frame(alignment: .center)
                        .frame(maxWidth: .infinity)
                        .foregroundColor(enabled ? .black : .gray)
                }
            }.disabled(!enabled)
        }
    }
}

private struct CheckButton :View {
    let isOn :Bool
    let text :String
    let action :() -> Void
    var body: some View {
        Button(action: action){
            HStack {
                Image(systemName: isOn ? "checkmark.square" : "square")
                    .foregroundColor(isOn ? .blue :.gray)
                Text(text)
                    .foregroundColor(.black)
                Spacer()
            }
        }.frame(maxWidth: .infinity)
    }
}

private struct DayButton :View {
    let day : Day
    let selected :Bool
    var update :() -> Void
    var body: some View {
        Button {
            update()
        } label: {
            Text(day == Day.none ? "" : day.text)
                .foregroundColor(.black)
                .frame(maxWidth: .infinity,alignment: .center)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.black, lineWidth: selected ? 1 : 0)
                )
        }
        
    }
}
