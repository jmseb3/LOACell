package com.wonddak.loacell.repository

data class AssetFilePlan(
    val allFileNames: Set<String>,
    val downloadFileNames: Set<String>,
)

interface AssetRepository {
    suspend fun createFilePlan(): AssetFilePlan

    fun isDownloaded(fileName: String): Boolean

    fun download(
        fileNames: Set<String>,
        onFileDownloaded: () -> Unit,
    )

    fun replace(
        fileName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    )

    suspend fun load(fileNames: Set<String>)
}
