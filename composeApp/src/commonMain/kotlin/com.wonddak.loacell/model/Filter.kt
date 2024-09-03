package com.wonddak.loacell.model

data class Filter(
    val raidType: Set<String> = emptySet(),
    val finish: FINISH = FINISH.ALL,
    val userList: Set<String> = emptySet(),
    val timeStep: Int = 60,
    val showEmptyCalendarRow: Boolean = false,
) {
    enum class FINISH(val title: String) {
        ALL("전체"),
        CLEAR("완료"),
        NOT_CLEAR("미완료"),
    }

    fun updateRaidType(type: String): Filter {
        val temp = raidType.toMutableSet()
        return if (temp.contains(type)) {
            temp.remove(type)
            this.copy(raidType = temp)
        } else {
            temp.add(type)
            this.copy(raidType = temp)
        }
    }

    fun updateFinish(finish: FINISH): Filter {
        return this.copy(finish = finish)
    }

    fun updateUser(user: String): Filter {
        val temp = userList.toMutableSet()
        return if (temp.contains(user)) {
            temp.remove(user)
            this.copy(userList = temp)
        } else {
            temp.add(user)
            this.copy(userList = temp)
        }
    }

    fun updateTimeStep(step: Int): Filter {
        return this.copy(timeStep = step)
    }

    fun updateEmptyCalendarRow(show: Boolean): Filter {
        return this.copy(showEmptyCalendarRow = show)
    }

    fun isSelectedType(type: String): Boolean {
        return this.raidType.contains(type)
    }

    fun isSelected(finish: FINISH): Boolean {
        return this.finish == finish
    }

    fun isSelectedUser(user: String): Boolean {
        return this.userList.contains(user)
    }

    fun clear(): Filter = Filter()

    val timeSteps: List<Int>
        get() = (0 until (1440 / timeStep)).map { it * timeStep }

    fun makeFilterList(
        raidInfoList: List<RaidInfo>,
        userInfoList: List<UserInfo>,
    ): List<RaidInfo> {
        val filterByFinish = when (finish) {
            FINISH.CLEAR -> raidInfoList.filter { it.isFinish }
            FINISH.NOT_CLEAR -> raidInfoList.filter { !it.isFinish }
            else -> raidInfoList
        }

        val filterByType = if (raidType.isEmpty()) {
            filterByFinish
        } else {
            filterByFinish.filter { raidType.contains(it.type) }
        }

        val filterByUser = if (userList.isEmpty()) {
            filterByType
        } else {
            filterByType.filter {
                val names = mutableListOf<String>()
                val filterUser = userInfoList.filter { userList.contains(it.name) }
                it.getAllPartyList().forEach { character ->
                    if (character.isNotEmpty()) {
                        for (userInfo in filterUser) {
                            val characterList = userInfo.characterList.map { it.name }
                            if (characterList.contains(character)) {
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

    fun makeTable(filterRaidInfoList: List<RaidInfo>): MutableList<MutableList<MutableList<RaidInfo>>> {
        val table = MutableList(24) { MutableList(60 / timeStep) { mutableListOf<RaidInfo>() } }

        filterRaidInfoList.forEach {
            if (it.day != Day.NONE) {
                table[it.hour][(it.minute / timeStep)].add(it)
            }
        }
        return table
    }
}