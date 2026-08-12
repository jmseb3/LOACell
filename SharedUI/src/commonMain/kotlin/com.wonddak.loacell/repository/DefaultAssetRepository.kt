package com.wonddak.loacell.repository

import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Synergy
import com.wonddak.loacell.assetData.Translate
import com.wonddak.loacell.model.RaidTypeItem
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.storage.AssetStorage
import com.wonddak.loacell.util.FileHelper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@ContributesBinding(AppScope::class)
@Inject
class DefaultAssetRepository(
    private val fbApi: FBApi,
    private val fileHelper: FileHelper,
    private val assetStorage: AssetStorage,
) : AssetRepository {

    override suspend fun createFilePlan(): AssetFilePlan {
        val allFileNames = fbApi.getAssetData()
            .mapTo(mutableSetOf()) { (key, value) -> "${key}_${value}.json" }
        return AssetFilePlan(
            allFileNames = allFileNames,
            downloadFileNames = allFileNames.filterTo(mutableSetOf()) {
                !fileHelper.isExistAsset(it)
            },
        )
    }

    override fun isDownloaded(fileName: String): Boolean = fileHelper.isExistAsset(fileName)

    override fun download(fileNames: Set<String>, onFileDownloaded: () -> Unit) {
        fileNames.forEach { fileName ->
            assetStorage.downloadAssetFile(
                fileName = fileName,
                fileHelper = fileHelper,
                successAction = onFileDownloaded,
            )
        }
    }

    override fun replace(fileName: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        if (!fileHelper.deleteAssetFile(fileName)) {
            onFailure()
            return
        }
        assetStorage.downloadAssetFile(
            fileName = fileName,
            fileHelper = fileHelper,
            successAction = onSuccess,
            failAction = { onFailure() },
        )
    }

    override suspend fun load(fileNames: Set<String>) {
        fileNames.forEach { fileName ->
            val savePath = fileHelper.getAssetFilePath(fileName)
            runCatching {
                val jsonString = fileHelper.readFile(savePath)
                when {
                    fileName.startsWith("raid_") -> {
                        val data: List<RaidTypeItem> = Json.decodeFromString(jsonString)
                        RaidItem.addData(data, assetStorage::getRaidImageUrl)
                    }

                    fileName.startsWith("synergy_") -> {
                        val data: Map<String, String> = Json.decodeFromString(jsonString)
                        Synergy.addData(data)
                    }

                    fileName.startsWith("translate_") -> {
                        val data: List<JsonElement> = Json.decodeFromString(jsonString)
                        Translate.addData(data)
                    }
                }
            }.onFailure {
                Napier.e(throwable = it) { "error" }
            }
        }
    }
}
