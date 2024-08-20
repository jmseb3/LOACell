package com.wonddak.loacell.model

data class RoomInfo(
    val uniqueId :String,
    val title :String,
    val description :String,
    val enterPassword :String,
    val enterUser : List<String>,
    val editableUser : List<String>
)