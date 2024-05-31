package com.wonddak.loacell.model

import io.github.aakira.napier.Napier


object Synergy {
    private var data : Map<String,String> = emptyMap()
    fun addData(data:Map<String,String>) {
        Napier.d { "synergyData Add : $data" }
        this.data = data
    }

    private fun getSynergy(className:String?) :String {
        className ?: return ""
        return if (data.contains(className)) {
            data[className]!!
        } else {
            ""
        }
    }

    fun getSynergyList(classList: List<String?>) : List<String> {
        return classList.map { getSynergy(it) }.filter { it.isNotEmpty() }.also {
            Napier.d { "getSynergyList : ${it.joinToString("/")}" }
        }
    }
}