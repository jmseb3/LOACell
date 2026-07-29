package com.wonddak.loacell.assetData

import com.wonddak.loacell.model.RaidData
import com.wonddak.loacell.model.RaidTypeItem
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object RaidItem {
    private const val TAG = "RaidItem"
    private var data: List<RaidTypeItem> = emptyList()

    fun getMapData(): Map<String, List<RaidData>> {
        val result = mutableMapOf<String, List<RaidData>>()
        for (datum in data) {
            result[datum.type] = datum.raidData
        }
        return result
    }

    fun getAllRaidData(): List<RaidData> {
        return data.flatMap { it.raidData }
    }

    private val imageCacheMap: MutableMap<String, String> = mutableMapOf()
    fun addData(
        data: List<RaidTypeItem>,
        getImageUrl: (type: String, success: (String) -> Unit) -> Unit,
    ) {
        data.forEach {
            it.raidData.forEach { raidData ->
                val type = raidData.name.lowercase()
                getImageUrl(type) { url ->
//                        Napier.d(tag = TAG) { "getAssetRaidImage : $url" }
                        imageCacheMap[type] = url
                }
            }
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

    fun getImage(type: String): String {
        if (imageCacheMap.containsKey(type)) {
            return imageCacheMap[type]!!
        }
        return ""
    }
}
