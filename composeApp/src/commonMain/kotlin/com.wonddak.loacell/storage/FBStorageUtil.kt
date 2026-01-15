package com.wonddak.loacell.storage

import com.wonddak.loacell.util.FileHelper

object FBStorageUtil {

    fun downloadFile(
        fileName :String,
        fileHelper : FileHelper,
        successAction : () -> Unit = {},
        failAction : (Error) -> Unit = {}
    ) {
        val savePath = fileHelper.getAssetFilePath(fileName)
        FireStorageReferenceHelper.getAssetReference(fileName)
            .downloadToFile(
                path = savePath,
                success = successAction,
                fail = failAction
            )
    }

    fun downloadFile(
        fileName :String,
        savePath :String,
        successAction : () -> Unit = {},
        failAction : (Error) -> Unit = {}
    ) {
        FireStorageReferenceHelper.getAssetReference(fileName)
            .downloadToFile(
                path = savePath,
                success = successAction,
                fail = failAction
            )
    }
}