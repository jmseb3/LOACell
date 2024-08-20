package com.wonddak.loacell.model

import com.wonddak.loacell.database.model.Day
import com.wonddak.loacell.database.model.Difficulty
import com.wonddak.loacell.database.model.RaidType

data class RaidInfo(
    val raidId :String,
    val roomId :String,
    val title :String,
    val type  :RaidType,
    val difficulty: Difficulty,
    val startGateNumber :Int,
    val endGateNumber :Int,
    val isFinish : Boolean,
    val party1characterList : List<String>,
    val party2characterList : List<String>,
    val party3characterList : List<String>,
    val party4characterList : List<String>,
    val day : Day,
    val hour : Int,
    val minute :Int
)