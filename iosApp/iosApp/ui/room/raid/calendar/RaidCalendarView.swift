//
//  RaidCalendarView.swift
//  iosApp
//
//  Created by WonHee Jung on 12/7/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

struct RaidCalendarView: View {
    let timeStep :Int
    let timeSteps : [Int]
    let showEmptyRow :Bool
    let table: [[[RaidInfo]]]
    
    let itemAction : (_ filterDay : [RaidInfo]) -> Void
    
    var body: some View {
        ScrollView {
            LazyVStack(
                pinnedViews: [.sectionHeaders]
            ) {
                Section(header : Header()) {
                    VStack {
                        ForEach(timeSteps,id:\.self) { totalMin in
                            let hour  = totalMin / 60
                            let minute  = (totalMin % 60)
                            let minuteIndex  = minute / timeStep
                            let result :[RaidInfo] = table[hour][minuteIndex]
                            if (result.isEmpty && !showEmptyRow) {
                                
                            } else {
                                CalendarRow(
                                    timeText: TimeHelper().makeTimeText(hour: Int32(hour), minute: Int32(minute), step: Int32(timeStep)),
                                    height: 60
                                ) { day in
                                    return makeCalendarText(result: result, day: day)
                                }
                            }
                        }
                        Divider()
                    }
                }
            }
        }.padding(.all,3)
    }
    
    func makeCalendarText(result :[RaidInfo],day :Day) -> String {
        let filterDay :[RaidInfo] = result.filter{ return $0.day == day }.sorted {
            $0.minute < $1.minute
        }
        let text = if (filterDay.isEmpty) {
            ""
        } else if (filterDay.count == 1) {
            filterDay[0].title
        } else {
            "\(filterDay[0].title) 외 \(filterDay.count - 1)"
        }
        return text
    }
}
struct Header: View {
    var body: some View {
        VStack {
            CalendarRow(timeText: "시간",height: 30) { day in
                return day.text
            }
            Divider()
        }.background(.white)
    }
}

struct CalendarRow : View {
    let timeText :String
    let height :CGFloat
    let content: (_ day : Day) -> String
    
    var body: some View {
        VStack {
            GeometryReader { reader in
                let size = reader.size.width / 11
                HStack {
                    HStack {
                        Text(timeText)
                            .frame(width: size * 2 - 1,alignment: .center)
                            .multilineTextAlignment(.center)
                            .font(.system(size: 13))

                        Divider()
                            .frame(width: 1)
                    }
                    .frame(width : size * 2)
                    
                    ForEach(Day.mon.getList(),id:\.name) { day in
                        HStack {
                            Text(content(day))
                                .frame(width: size - 1, alignment: .center)
                                .font(.system(size: 11))

                            Divider()
                                .frame(width: 1)
                        }.frame(width : size)
                    }
                  
                }
                .padding(.vertical,2)
                Divider()
            }
        }
        .frame(maxWidth: .infinity, minHeight: height)
    }
}
