package com.wonddak.loacell.storage

expect class CommonFireStorage
expect class CommonStorageReference
expect class FSError

//FireStorage를 가져온다
expect fun getFireStorage(): CommonFireStorage

//FireStorage로부터 Reference를 가져온다.
expect fun CommonFireStorage.getCommonReference() : CommonStorageReference

//Reference로부터 Child를 가져온다
expect fun CommonStorageReference.getChildPath(path:String) : CommonStorageReference

expect fun CommonStorageReference.downloadToFile(
    path: String,
    successCompletion: () -> Unit,
    failCompletion: (error: FSError) -> Unit,
)

object FireStorageReferenceHelper {
    private val storage : CommonFireStorage = getFireStorage()

    fun getAssetReference(fileName: String): CommonStorageReference {
        return storage.getCommonReference().getChildPath("dataFiles/$fileName")
    }
}
