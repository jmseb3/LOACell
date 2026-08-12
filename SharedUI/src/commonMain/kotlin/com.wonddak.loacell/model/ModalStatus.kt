package com.wonddak.loacell.model

interface Modal {
    val title: String
}

enum class Dialog(override val title: String) : Modal {
    ROOM_EXIT("방 나가기"),
    RAID_DELETE("레이드 정보 삭제"),
    CHARACTER_EDIT("대표 캐릭터 변경"),
    CHARACTER_DELETE("유저 정보 삭제"),
    SETTING_EDIT_NAME("이름 변경"),
}

enum class Sheet(override val title: String) : Modal {
    ROOM_ADD("방 만들기"),
    ROOM_EDIT("수정하기"),
    USER_ADD("유저 정보 추가"),
    RAID_FILTER("필터 설정"),
    RAID_USER_ADD("캐릭터 정보 추가"),
    SHARE_SHEET("공유하기"),
}
