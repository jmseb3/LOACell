package com.wonddak.loacell.util

import com.wonddak.loacell.storage.FSError
import com.wonddak.loacell.storage.FireStorageReferenceHelper
import com.wonddak.loacell.storage.downloadToFile

object FBStorageUtil {

    fun downloadFile(
        fileName :String,
        fileHelper : FileHelper,
        successAction : () -> Unit = {},
        failAction : (FSError) -> Unit = {}
    ) {
        val savePath = fileHelper.getAssetFilePath(fileName)
        FireStorageReferenceHelper.getAssetReference(fileName)
            .downloadToFile(
                savePath,
                successCompletion = successAction,
                failCompletion = failAction
            )
    }

    fun downloadFile(
        fileName :String,
        savePath :String,
        successAction : () -> Unit = {},
        failAction : (FSError) -> Unit = {}
    ) {
        FireStorageReferenceHelper.getAssetReference(fileName)
            .downloadToFile(
                savePath,
                successCompletion = successAction,
                failCompletion = failAction
            )
    }
}