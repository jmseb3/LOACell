package com.wonddak.loacell.model

enum class RoomRole(val toName :String) {
    OWNER("소유자"),
    MANAGER("관리자"),
    USER("일반 유저"),
    NONE(" -")
}