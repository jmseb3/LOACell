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
                successAction()
            }
    }
}

private struct RaidSheetBase: View {
    @Binding var fbRaidInfo :FBRaidInfo
    let title :String
    let buttonText :String
    let onDismiss :() -> Void
    let buttonAction :() -> Void
    
    @State private var showDayUse :Bool = false
    @State private var titleText : String = ""
    @State private var raidType : RaidType  = RaidType.etc
    @State private var abStart :Int = 1
    @State private var abEnd :Int = 1
    
    @State private var showGateEdit : Bool = false
    
    var body: some View {
        BaseSheet(
            title: title,
            text: buttonText,
            action: {
                buttonAction()
            },
            enabled: .constant(true),
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
                    Picker("레이드 정보 선택",selection: $raidType) {
                        ForEach(RaidType.entries, id:\.self) { item in
                            Text(item.toKorString())
                        }
                    }.onAppear{
                        raidType = fbRaidInfo.type
                    }
                    
                }
                VStack{
                    RaidSheetHeadeerText(text: "난이도 선택")
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
                                Button(
                                    action: {
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
                                ){
                                    HStack {
                                        Image(systemName: isOn ? "checkmark.square" : "square")
                                            .foregroundColor(isOn ? .blue :.gray)
                                        Text("\(idx)")
                                            .foregroundColor(.black)
                                        Spacer()
                                    }
                                }.frame(maxWidth: .infinity)
                            }
                        }
                    }
                    .onAppear{
                        abStart = 1
                        abEnd = 1
                    }
                    .animation(.spring, value : showGateEdit)
                }
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
}

private struct RaidSheetHeadeerText : View {
    let text : String
    var body: some View {
        VStack {
            Spacer().frame(height: 10)
            Text(text)
                .font(.title3)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity,alignment: .leading)
            Divider()
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
