package com.wonddak.loacell.storage

import com.wonddak.loacell.util.FileHelper
import dev.zacsweers.metro.Inject

@Inject
class AssetStorage(
    private val storage: CommonFireStorage,
) {
    fun downloadAssetFile(
        fileName: String,
        fileHelper: FileHelper,
        successAction: () -> Unit = {},
        failAction: (FSError) -> Unit = {},
    ) {
        downloadAssetFile(
            fileName = fileName,
            savePath = fileHelper.getAssetFilePath(fileName),
            successAction = successAction,
            failAction = failAction,
        )
    }

    fun downloadAssetFile(
        fileName: String,
        savePath: String,
        successAction: () -> Unit = {},
        failAction: (FSError) -> Unit = {},
    ) {
        assetReference(fileName).downloadToFile(
            path = savePath,
            successCompletion = successAction,
            failCompletion = failAction,
        )
    }

    fun getRaidImageUrl(type: String, success: (String) -> Unit) {
        storage.getCommonReference()
            .getChildPath("dataFiles/raid/raid_$type.png")
            .getDownloadUrl(success)
    }

    private fun assetReference(fileName: String): CommonStorageReference = storage
        .getCommonReference()
        .getChildPath("dataFiles/$fileName")
}
