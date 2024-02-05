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
    let addAction : (FBRaidInfo) -> Void
    var body: some View {
        RaidSheetBase(
            fbRaidInfo: $fbRaidInfo,
            title: "레이드 정보 추가",
            buttonText: "추가"
        ) {
            addAction(fbRaidInfo)
        }
    }
}

struct EditRaidSheet: View {
    let raidInfo :RaidInfo
    let editAction : (FBRaidInfo) -> Void
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
    var body: some View {
        RaidSheetBase(
            fbRaidInfo: $fbRaidInfo,
            title: "레이드 정보 수정",
            buttonText: "수정"
        ) {
            editAction(fbRaidInfo)
        }.onAppear {
            print(raidInfo)
            fbRaidInfo =  FBRaidInfo(
                title: raidInfo.title,
                type: raidInfo.type,
                difficulty: raidInfo.Difficulty,
                startGateNumber: Int32(raidInfo.startGateNumber),
                endGateNumber: Int32(raidInfo.endGateNumber),
                isFinish: raidInfo.isFinish,
                party1: raidInfo.party1characterList,
                party2: raidInfo.party2characterList,
                day: raidInfo.day,
                hour: raidInfo.hour,
                minute: raidInfo.minute
            )
        }
    }
}

private struct RaidSheetBase: View {
    @Binding var fbRaidInfo :FBRaidInfo
    let title :String
    let buttonText :String
    let buttonAction :() -> Void
    
    @State private var titleText : String = ""
    @State private var abStart :Int = 1
    @State private var abEnd :Int = 1
    
    @State private var dayHour :Int = 0
    @State private var dayMin :Int = 0
    
    private var showGateEdit : Bool {
        (fbRaidInfo.type == RaidType.abrelshud) && (fbRaidInfo.difficulty != Difficulty.hell)
    }
    @State private var showDayUse :Bool = false
    @State private var dayPick : Day = Day.none
    
    private let days = Day.entries
    
    private var difList: [Difficulty]  {
        if (Const().useExtreme) {
            [Difficulty.normal,Difficulty.hard,Difficulty.hell,Difficulty.extremenormal,Difficulty.extremehard]
        } else {
            [Difficulty.normal,Difficulty.hard,Difficulty.hell]
        }
    }
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
                Form {
                    Section {
                        Picker(
                            "레이드",
                            selection: Binding {
                                fbRaidInfo.type
                            } set: { type in
                                fbRaidInfo = fbRaidInfo.updateType(type: type)
                            }
                        ) {
                            ForEach(RaidType.entries, id:\.self) { item in
                                Text(item.toKorString()).tag(item)
                            }
                        }
                        .accentColor(.black)
                        Picker(
                            "난이도",
                            selection: Binding {
                                fbRaidInfo.difficulty
                            } set: { dif in
                                fbRaidInfo = fbRaidInfo.updateDifficulty(difficulty: dif)
                            }
                        ) {
                            ForEach(difList, id:\.self) { item in
                                let enabled = fbRaidInfo.difficultyEnabled(difficulty :item)
                                if enabled {
                                    Text(item.toKorString()).tag(item)
                                }
                            }
                        }
                        .pickerStyle(SegmentedPickerStyle())
                        .accentColor(.black)
                    } header: {
                        Text("레이드 선택")
                    }
                    
                    Section {
                        HStack {
                            Text("입장 레벨")
                            Spacer()
                            Text(fbRaidInfo.getMinLevelText())
                        }
                        HStack {
                            Text("입장 인원")
                            Spacer()
                            Text(String(fbRaidInfo.type.maxPerson))
                        }
                        
                    } header: {
                        Text("레이드 정보")
                    }
                    Button {
                        withAnimation {
                            showDayUse = !showDayUse
                            if !showDayUse {
                                dayPick = Day.none
                            }
                        }
                    } label: {
                        Text("일정 지정")
                    }
                    if showDayUse {
                        Section {
                            Picker("시", selection: $dayHour) {
                                ForEach(Array(0...23), id: \.self) { hour in
                                    Text(String(format: "%02d", hour))
                                }
                            }
                            Picker("분", selection: $dayMin) {
                                ForEach(Array(0...59), id: \.self) { min in
                                    Text(String(format: "%02d", min))
                                }
                            }
                            Picker(
                                "요일",
                                selection: $dayPick
                            ) {
                                ForEach(1 ..< days.count) {
                                    Text(days[$0].text).tag(days[$0])
                                }
                            }
                            .pickerStyle(SegmentedPickerStyle())
                        } header: {
                            Text("레이드 일정 선택")
                        }
                    }
                }
                .frame(height: 350)
            }
            .onChange(of: titleText) { value in
                fbRaidInfo = fbRaidInfo.updateTitle(title: value)
            }
            .onChange(of: dayMin) {value in
                fbRaidInfo = fbRaidInfo.updateTimeMinute(minute : Int64(value))
            }
            .onChange(of: dayHour) {value in
                fbRaidInfo = fbRaidInfo.updateTimeHour(hour : Int64(value))
            }
            .onChange(of: dayPick) { value in
                updateDay(day: value)
            }
            .onAppear {
                titleText = fbRaidInfo.title
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
