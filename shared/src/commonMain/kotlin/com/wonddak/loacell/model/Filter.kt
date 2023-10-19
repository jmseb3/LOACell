package com.wonddak.loacell.model

import com.wonddak.database.model.RaidType

data class Filter(
    val raidType: List<RaidType> = RaidType.values().toList(),
    val finish: FINISH = FINISH.ALL,
    val userList: List<String> = emptyList(),
    val timeStep : Int = 60,
    val showEmptyCalendarRow :Boolean = false
) {
    companion object {
        fun getInitFilter() :Filter = Filter()
    }

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
}