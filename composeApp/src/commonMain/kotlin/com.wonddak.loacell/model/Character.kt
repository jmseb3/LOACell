package com.wonddak.loacell.model

data class Character(
    val userName: String,
    val roomId: String,
    val name: String,
    val server: String,
    val className: String,
    val level: String
) {
    fun getLevel(): Float {
        return this.level.replace(",", "").toFloat()
    }
}