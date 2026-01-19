package com.wonddak.loacell.storage

import com.wonddak.loacell.core.firebase.storage.CommonFireStorage
import com.wonddak.loacell.core.firebase.storage.CommonStorageReference
import com.wonddak.loacell.core.firebase.storage.getChildPath
import com.wonddak.loacell.core.firebase.storage.getCommonReference
import com.wonddak.loacell.core.firebase.storage.getFireStorage
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object FireStorageReferenceHelper {
    private val storage: CommonFireStorage = getFireStorage()

    fun getAssetReference(fileName: String): CommonStorageReference {
        return storage
            .getCommonReference()
            .getChildPath("dataFiles/$fileName")
    }

    fun getAssetRaidImage(type: String): CommonStorageReference {
        return storage
            .getCommonReference()
            .getChildPath("dataFiles/raid/raid_$type.png")
    }
}
