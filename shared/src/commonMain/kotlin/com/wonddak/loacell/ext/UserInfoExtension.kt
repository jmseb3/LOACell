package com.wonddak.loacell.ext

import com.wonddak.loacell.Character
import com.wonddak.loacell.UserInfo

fun UserInfo.checkTimeOver(nowTime: Long): Boolean {
    return (nowTime - this.timeStamp!!) / 1000 >= 3_600
}

fun UserInfo.containCharacterName(name:String) : Boolean {
    return  this.characterList.map { it.name }.contains(name)
}

fun UserInfo.getCharacterListSort() : List<Character> {
    val result = this.characterList.sortedByDescending { it.level.replace(",","").toFloat() }.toMutableList()
    val find = result.find { it.name == this.representativeCharacter }!!
    result.remove(find)
    result.add(0,find)
    return result
}

fun UserInfo.getCharacterInfo(name:String) : Character? {
    return this.characterList.find { it.name == name }
}

fun List<UserInfo>.findCharacter(name:String) : Character? {
    if (name == "") {
        return  null
    }
    for (userInfo in this) {
        val find = userInfo.getCharacterInfo(name)
        if (find != null) {
            return  find
        }
    }
    return null
}

fun List<UserInfo>.findCharacters(names:List<String>) : List<Character?> {
    val result : MutableList<Character?> = List(names.size) { null }.toMutableList()

    val findNames = names.filter { it.isNotEmpty() }.toMutableList()

    for (userInfo in this) {
        val iterator = findNames.iterator()
        while (iterator.hasNext()) {
            val name = iterator.next()
            val find = userInfo.getCharacterInfo(name)
            if (find != null) {
                result[names.indexOf(name)] = find
            }
        }
    }
    return result
}