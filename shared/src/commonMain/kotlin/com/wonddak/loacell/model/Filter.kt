package com.wonddak.loacell.model

import com.wonddak.database.AppDataBase
import com.wonddak.database.model.RaidType
import com.wonddak.loacell.RaidInfo
import com.wonddak.loacell.UserInfo
import com.wonddak.loacell.ext.getAllPartyList

data class Filter(
    val raidType: List<RaidType> = RaidType.values().toList(),
    val finish: FINISH = FINISH.ALL,
    val userList: List<String> = emptyList()
) {

    enum class FINISH(val title:String) {
        ALL("전체"),CLEAR("완료"),NOT_CLEAR("미완료"),
    }

    fun filterList(
        raidInfoList: List<RaidInfo>,
        userInfoList: List<UserInfo>,
        db: AppDataBase
    ): List<RaidInfo> {
        val filterByFinish = when (finish) {
            FINISH.CLEAR -> raidInfoList.filter { it.isFinish }
            FINISH.NOT_CLEAR -> raidInfoList.filter { !it.isFinish }
            else -> raidInfoList
        }
        val filterByType =
            filterByFinish.filter { raidType.contains(it.type) }

        val filterByUser = if (userList.isEmpty()) {
            filterByType
        } else {
            filterByType.filter {
                val names = mutableListOf<String>()
                val filterUser = userInfoList.filter { userList.contains(it.name) }
                it.getAllPartyList().forEach { character ->
                    if (character.isNotEmpty()) {
                        for (userInfo in filterUser) {
                            if (db.characterQueriesHelper.getAllNameList(userInfo)
                                    .contains(character)
                            ) {
                                names.add(userInfo.name)
                                break
                            }
                        }
                    }
                }
                names.sorted() == userList.sorted()
            }
        }

        return filterByUser
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