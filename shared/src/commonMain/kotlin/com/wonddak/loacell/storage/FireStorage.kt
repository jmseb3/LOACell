package com.wonddak.loacell.storage

import io.github.aakira.napier.Napier
import kotlinx.serialization.json.JsonElement

expect class CommonFireStorage
expect class CommonStorageReference

expect class ByteData
expect class FSError

//FireStorage를 가져온다
expect fun getFireStorage(): CommonFireStorage

//FireStorage로부터 Reference를 가져온다.
expect fun CommonFireStorage.getCommonReference() : CommonStorageReference

//Reference로부터 Child를 가져온다
expect fun CommonStorageReference.getChildPath(path:String) : CommonStorageReference

//Reference로부터 downloadByte를 한다.
expect fun CommonStorageReference.downloadByByte(
    successCompletion: (data:ByteData?) -> Unit,
    failCompletion :(error : FSError) -> Unit
)

internal const val SynergyJson = "dataFiles/synergy.json"
internal const val ONE_MEGABYTE: Long = 1024 * 1024

object CommonFireStorageHelper {
    private val storage : CommonFireStorage = getFireStorage()

    private fun getSynergyReference(): CommonStorageReference {
        return storage.getCommonReference().getChildPath(SynergyJson)
    }
    fun parseSynergyJson() {
        getSynergyReference()
            .downloadByByte(
                successCompletion = {
                    Napier.v(tag = "CommonFireStorageHelper1") { it.toString() }
                    val jsonString = it?.toJson()
                    Napier.v(tag = "CommonFireStorageHelper2") { jsonString.toString() }
                },
                failCompletion = {
                    Napier.e(tag = "CommonFireStorageHelper") {it.toString()}
                }
            )
    }
}

expect fun ByteData.toJson():JsonElement