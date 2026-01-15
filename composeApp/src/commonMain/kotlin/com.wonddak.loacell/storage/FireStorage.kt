package com.wonddak.loacell.storage

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FireStorageReferenceHelper {
    private val storage : CommonFireStorage = getFireStorage()

    fun getAssetReference(fileName: String): CommonStorageReference {
        return storage.getCommonReference().getChildPath("dataFiles/$fileName")
    }
    fun getAssetRaidImage(type: String): CommonStorageReference {
        return storage.getCommonReference().getChildPath("dataFiles/raid/raid_$type.png")
    }
}
