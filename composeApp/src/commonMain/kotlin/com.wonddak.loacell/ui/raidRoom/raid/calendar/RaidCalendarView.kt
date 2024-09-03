package com.wonddak.loacell.android.ui.room.raid.calendar

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun RaidCalendarView(
//    timeStep: Int,
//    timeSteps: List<Int>,
//    showEmptyRow: Boolean,
//    table: List<List<List<RaidInfo>>>,
//    itemClick: (filterDay: List<RaidInfo>) -> Unit
//) {
//    fun makeCalendarText(result : List<RaidInfo>,day:Day): Pair<String,() -> Unit> {
//        val filterDay = result.filter { it.day == day }.sortedBy { it.minute }
//        val text = if (filterDay.isEmpty()) {
//            ""
//        } else if (filterDay.size == 1) {
//            filterDay.first().title
//        } else {
//            "${filterDay.first().title} 외 ${filterDay.size - 1}"
//        }
//        return Pair(text) {itemClick(filterDay)}
//    }
//
//    val listState = rememberLazyListState()
//
//    LaunchedEffect(timeStep) {
//        listState.scrollToItem(0)
//    }
//
//    LazyColumn(state = listState) {
//        stickyHeader {
//            Column() {
//                CalendarRow(
//                    modifier = Modifier.background(Color.White),
//                    timeText = "시간"
//                ) { day ->
//                    Pair(day.text) {  }
//                }
//            }
//        }
//        items(timeSteps) { totalMin ->
//            val hour = totalMin / 60
//            val minute = (totalMin % 60)
//            val minuteIndex = (totalMin % 60) / timeStep
//            val result = table[hour][minuteIndex]
//
//            if (result.isEmpty() && !showEmptyRow) {
//                return@items
//            }
//            CalendarRow(
//                timeText = TimeHelper.makeTimeText(
//                    hour,
//                    minute,
//                    timeStep
//                )
//            ) { day ->
//                makeCalendarText(result,day)
//            }
//        }
//    }
//}
//
//@Composable
//fun CalendarRow(
//    modifier: Modifier = Modifier,
//    timeText: String,
//    dayItem: (day: Day) -> Pair<String, () -> Unit>
//) {
//    Column {
//        Row(
//            modifier = modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = timeText,
//                modifier = Modifier.weight(2f),
//                textAlign = TextAlign.Center,
//                fontSize = 13.sp
//            )
//            Divider(
//                modifier = Modifier
//                    .height(20.dp)
//                    .width(1.dp)
//            )
//            Row(
//                modifier = Modifier
//                    .wrapContentHeight()
//                    .weight(7f),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Day.MON.getList().forEach { day ->
//                    val (text, action) = dayItem(day)
//                    Text(
//                        modifier = Modifier
//                            .weight(1f)
//                            .noRippleClickable {
//                                action()
//                            },
//                        text = text,
//                        textAlign = TextAlign.Center,
//                        fontSize = 11.sp
//                    )
//                    Divider(
//                        modifier = Modifier
//                            .height(20.dp)
//                            .width(1.dp)
//                    )
//                }
//            }
//        }
//        Divider()
//    }
//
//}