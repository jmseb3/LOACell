package com.wonddak.loacell.storage

import kotlinx.serialization.json.Json

expect class CommonFireStorage
expect class CommonStorageReference
expect class FSError

//FireStorage를 가져온다
expect fun getFireStorage(): CommonFireStorage

//FireStorage로부터 Reference를 가져온다.
expect fun CommonFireStorage.getCommonReference() : CommonStorageReference

//Reference로부터 Child를 가져온다
expect fun CommonStorageReference.getChildPath(path:String) : CommonStorageReference


internal const val SynergyJson = "dataFiles/synergy.json"

object FireStorageReferenceHelper {
    private val storage : CommonFireStorage = getFireStorage()

    fun getSynergyReference(): CommonStorageReference {
        return storage.getCommonReference().getChildPath(SynergyJson)
    }

    fun jsonStringToData(jsonStr:String) : Map<String,String> {
        val res = Json.decodeFromString<Map<String, String>>(jsonStr)
        return res
    }
}


internal const val FileName = "synergy.json"
expect class SynergyReferenceHelper() {
    fun isExist() :Boolean

    fun downloadFile(callBack:(Map<String,String>) -> Unit)
}
