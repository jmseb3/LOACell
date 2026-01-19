package com.wonddak.loacell.util

import kotlin.random.Random

object NameHelper {
    private val nameList = listOf("코니", "모코코", "디붕디붕")
    fun makeName(): String {
        val st = StringBuilder()
            .append(nameList.random())
            .append(Random.nextInt(8) + 1)
            .append(Random.nextInt(8) + 1)
            .append(Random.nextInt(8) + 1)
            .append(Random.nextInt(8) + 1)
            .toString()
        return st
    }
}