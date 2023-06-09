package com.wonddak.loacell

enum class RoomRole(val toName :String) {
    OWNER("소유자"),
    MANAGER("관리자"),
    USER("일반 유저"),
    ANONYMOUS("익명 유저"),
    NONE(" -")
}