package com.wonddak.loacell.model

import com.wonddak.loacell.database.model.Day
import com.wonddak.loacell.database.model.RaidType

data class Filter(
    val raidType: List<RaidType> = RaidType.entries,
    val finish: FINISH = FINISH.ALL,
    val userList: List<String> = emptyList(),
    val timeStep : Int = 60,
    val showEmptyCalendarRow :Boolean = false
) {
    enum class FINISH(val title:String) {
        ALL("전체"),CLEAR("완료"),NOT_CLEAR("미완료"),
    }

    fun updateRaidType(type: RaidType): Filter {
        val temp = raidType.toMutableList()
        return if (temp.contains(type)) {
            temp.remove(type)
            this.copy(raidType = temp)
        } else {
            temp.add(type)
            this.copy(raidType = temp)
        }
    }


    fun updateFinish(finish: FINISH) :Filter {
        return this.copy(finish = finish)
    }

    fun updateUser(user: String): Filter {
        val temp = userList.toMutableList()
        return if (temp.contains(user)) {
            temp.remove(user)
            this.copy(userList = temp)
        } else {
            temp.add(user)
            this.copy(userList = temp)
        }
    }

    fun updateTimeStep(step:Int) : Filter {
        return this.copy(timeStep = step)
    }
    fun updateEmptyCalendarRow(show:Boolean) : Filter {
        return this.copy(showEmptyCalendarRow = show)
    }

    fun isSelected(type: RaidType) :Boolean {
        return  this.raidType.contains(type)
    }

    fun isSelected(finish: FINISH) :Boolean {
        return  this.finish == finish
    }
    fun isSelected(user: String) :Boolean {
        return  this.userList.contains(user)
    }

    fun clear() : Filter = Filter()

    val timeSteps : List<Int>
        get() = (0 until (1440 / timeStep)).map { it * timeStep }
    fun makeTable(filterRaidInfoList :List<RaidInfo>) : MutableList<MutableList<MutableList<RaidInfo>>> {
        val table =  MutableList(24) { MutableList(60 / timeStep) { mutableListOf<RaidInfo>() } }

        filterRaidInfoList.forEach {
            if (it.day != Day.NONE) {
                table[it.hour.toInt()][(it.minute / timeStep).toInt()].add(it)
            }
        }
        return table
    }

}