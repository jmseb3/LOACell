package com.wonddak.loacell.repository

data class AssetFilePlan(
    val allFileNames: Set<String>,
    val downloadFileNames: Set<String>,
)

interface AssetRepository {
    suspend fun createFilePlan(): AssetFilePlan

    fun download(
        fileNames: Set<String>,
        onFileDownloaded: () -> Unit,
    )

    suspend fun load(fileNames: Set<String>)
}
