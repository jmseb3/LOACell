package com.wonddak.loacell.assetData

import com.wonddak.loacell.model.Character
import io.github.aakira.napier.Napier
import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object Synergy {
    private const val TAG = "Synergy"
    private var data: Map<String, String> = emptyMap()
    fun addData(data: Map<String, String>) {
//        Napier.d(tag = TAG) { "synergyData Add : $data" }
        Synergy.data = data
    }

    private fun getSynergy(className: String?): String {
        className ?: return ""
        return if (data.contains(className)) {
            data[className]!!
        } else {
            ""
        }
    }

    private fun getSynergy(classList: List<String?>): List<String> {
        Napier.d(tag = TAG) { "getItem : ${classList.joinToString("/")}" }

        return classList.map { getSynergy(it) }.filter { it.isNotEmpty() }.also {
            Napier.d(tag = TAG) { "getSynergyList : ${it.joinToString("/")}" }
        }
    }

    fun getSynergyList(characterList: List<Character?>): List<String> {
        return getSynergy(classList = characterList.map { it?.className })
    }
}