package com.wonddak.loacell.assetData

import com.wonddak.loacell.model.RaidData
import com.wonddak.loacell.model.RaidTypeItem
import com.wonddak.loacell.storage.FireStorageReferenceHelper
import com.wonddak.loacell.storage.getDownloadUrl
import io.github.aakira.napier.Napier

object RaidItem {
    private const val TAG = "RaidItem"
    private var data: List<RaidTypeItem> = emptyList()

    private val imageCacheMap: MutableMap<String, String> = mutableMapOf()
    fun addData(data: List<RaidTypeItem>) {
        data.forEach {
            it.raidData.forEach { raidData ->
                val type = raidData.name.lowercase()
                FireStorageReferenceHelper
                    .getAssetRaidImage(type)
                    .getDownloadUrl { url ->
                        imageCacheMap[type] = url
                    }
            }
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

    fun getImage(type: String): String {
        if (imageCacheMap.containsKey(type)) {
            return imageCacheMap[type]!!
        }
        return ""
    }
}