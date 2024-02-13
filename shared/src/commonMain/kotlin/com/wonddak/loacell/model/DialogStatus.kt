package com.wonddak.loacell.model

enum class DialogStatus {
    NONE,
    ROOM_ACTION,
    ROOM_ADD,
    ROOM_ENTER,
    ROOM_ENTER_BY_SCHEME,
    ROOM_ENTER_ERROR,
    ROOM_EXIT,
    ROOM_EDIT,
    USER_ADD,
    RAID_ADD,
    RAID_EDIT,
    RAID_FILTER,
    RAID_DELETE,
    RAID_USER_ADD,
    RAID_USER_DELETE,
    CHARACTER_EDIT,
    CHARACTER_DELETE,
    SETTING_EDIT_NAME,
    SHARE_SHEET,
    TEST_SHEET
//
//    fun sheetType(): Set<DialogStatus> = setOf(
//        ROOM_ADD,
//        ROOM_EDIT,
//        USER_ADD,
//        RAID_ADD,
//        RAID_EDIT,
//        RAID_FILTER,
//        RAID_USER_ADD,
//        SHARE_SHEET
//    )
//    fun dialogType(): Set<DialogStatus> = setOf(
//        ROOM_ACTION,
//        ROOM_ENTER,
//        ROOM_ENTER_ERROR,
//        ROOM_EXIT,
//        RAID_DELETE,
//        RAID_USER_DELETE,
//        CHARACTER_EDIT,
//        CHARACTER_DELETE,
//        SETTING_EDIT_NAME
//    )
}