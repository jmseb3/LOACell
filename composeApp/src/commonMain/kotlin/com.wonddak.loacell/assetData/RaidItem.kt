package com.wonddak.loacell.assetData

import com.wonddak.loacell.model.RaidData
import com.wonddak.loacell.model.RaidTypeItem
import io.github.aakira.napier.Napier

object RaidItem {
    private const val TAG = "RaidItem"
    private var data: List<RaidTypeItem> = emptyList()

    fun addData(data: List<RaidTypeItem>) {
        data.forEach {
            Napier.d(tag = TAG) { "RaidItem Add : $data" }
        }
        this.data = data
    }

    private val cacheMap: MutableMap<String, RaidData> = mutableMapOf()

    fun findByName(name: String): RaidData? {
        if (cacheMap.containsKey(name)) {
            return cacheMap[name]!!
        } else {
            val result: RaidData? = data.flatMap { it.raidData }.find { it.name == name }
            if (result == null) {
                return null
            } else {
                cacheMap[name] = result
                return result
            }
        }
    }
}