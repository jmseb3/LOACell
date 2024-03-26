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
    let addAction : (FBRaidInfo) -> Void
    var body: some View {
        RaidSheetBase(
            raidInfo : nil,
            title: "레이드 정보 추가",
            buttonText: "추가",
            buttonAction : addAction
        )
    }
}

struct EditRaidSheet: View {
    let raidInfo :RaidInfo
    let editAction : (FBRaidInfo) -> Void
    var body: some View {
        RaidSheetBase(
            raidInfo : raidInfo,
            title: "레이드 정보 수정",
            buttonText: "수정",
            buttonAction : editAction
        )
    }
}

private struct RaidSheetBase: View {
    
    let raidInfo :RaidInfo?
    let title :String
    let buttonText :String
    let buttonAction :(FBRaidInfo) -> Void
    
    @State private var fbRaidInfo = FBRaidInfo(
        title: "",
        type: RaidType.valtan,
        difficulty: Difficulty.normal,
        startGateNumber: 1,
        endGateNumber: 1,
        isFinish: false,
        party1: [String](repeating: "", count: 4),
        party2: [String](repeating: "", count: 4),
        party3: [String](repeating: "", count: 4),
        party4: [String](repeating: "", count: 4),
        day: Day.none,
        hour: 0,
        minute: 0
    )
    @State private var showDayUse :Bool = false
    
    private var showGateEdit : Bool {
        (fbRaidInfo.type == RaidType.abrelshud) && (fbRaidInfo.difficulty != Difficulty.hell)
    }
        
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
                buttonAction(fbRaidInfo)
            },
            enabled: !fbRaidInfo.title.isEmpty && (showDayUse ? fbRaidInfo.day != Day.none : true),
            errorMsg: .constant("")
        ) {
            VStack {
                LengthLimitTextField(
                    maxLength: 10,
                    placeHolder: "제목을 입력해주세요.",
                    text: Binding {
                        fbRaidInfo.title
                    } set: { title in
                        fbRaidInfo = fbRaidInfo.updateTitle(title: title)
                    }
                )
                if showGateEdit {
                    VStack{
                        RaidSheetHeadeerText(text: "관문 선택")
                        HStack {
                            var abStart = fbRaidInfo.startGateNumber
                            var abEnd = fbRaidInfo.endGateNumber
                            let arr :[Int32] = [1,2,3,4]
                            ForEach(arr,id:\.self) { idx in
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
                        fbRaidInfo = fbRaidInfo.updateGate(start:Int32(1),end:Int32(1))
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
                        LabeledContent("입장 레벨", value: fbRaidInfo.getMinLevelText())
                        LabeledContent("입장 인원", value: String(fbRaidInfo.type.maxPerson))
                    } header: {
                        Text("레이드 선택")
                    }
                    
                    Section {
                        Toggle("일정 지정", isOn: $showDayUse)
                        if showDayUse {
                            Picker("시", selection: Binding {
                                fbRaidInfo.hour
                            } set: { value in
                                fbRaidInfo = fbRaidInfo.updateTimeHour(hour : value)
                            }) {
                                ForEach(0..<23) {
                                    Text(String(format: "%02d", $0)).tag(Int64($0))
                                }
                            }
                            Picker("분", selection: Binding {
                                fbRaidInfo.minute
                            } set: { value in
                                fbRaidInfo = fbRaidInfo.updateTimeMinute(minute : value)
                            }) {
                                ForEach(0 ..< 59) {
                                    Text(String(format: "%02d", $0)).tag(Int64($0))
                                }
                            }
                            Picker(
                                "요일",
                                selection: Binding {
                                    fbRaidInfo.day
                                } set: { value in
                                    fbRaidInfo = fbRaidInfo.updateDay(day:value)
                                }
                            ) {
                                ForEach(1 ..< days.count) {
                                    Text(days[$0].text).tag(days[$0])
                                }
                            }
                            .pickerStyle(SegmentedPickerStyle())
                            .onDisappear {
                                fbRaidInfo = fbRaidInfo.resetDay()
                            }
                        }
                    }
                }
                .frame(height: 350)
            }
        }
        .onChange(of: fbRaidInfo) { newValue in
            print("update \(newValue)")
        }
        .onAppear {
            guard let raidInfo = raidInfo else {
                return
            }
            self.fbRaidInfo = FBRaidInfo(
                title: raidInfo.title,
                type: raidInfo.type,
                difficulty: raidInfo.Difficulty,
                startGateNumber: Int32(raidInfo.startGateNumber),
                endGateNumber: Int32(raidInfo.endGateNumber),
                isFinish: raidInfo.isFinish,
                party1: raidInfo.party1characterList,
                party2: raidInfo.party2characterList,
                party3: raidInfo.party3characterList,
                party4: raidInfo.party4characterList,
                day: raidInfo.day,
                hour: raidInfo.hour,
                minute: raidInfo.minute
            )
            self.showDayUse = fbRaidInfo.day != Day.none
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
