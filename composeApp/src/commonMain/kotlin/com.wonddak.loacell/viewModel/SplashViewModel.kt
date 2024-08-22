package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.assetData.RaidItem
import com.wonddak.loacell.assetData.Synergy
import com.wonddak.loacell.model.RaidTypeItem
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.storage.FireStorageReferenceHelper
import com.wonddak.loacell.storage.downloadToFile
import com.wonddak.loacell.util.FileHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SplashViewModel(
    private val fbApi: FBApi,
    private val fileHelper: FileHelper,
) : ViewModel() {

    var timeCheck by mutableStateOf(false)
        private set

    var downloadCheck by mutableStateOf(false)
        private set

    var totalFileName = mutableSetOf<String>()
    var downloadFileSet = mutableSetOf<String>()

    fun startCheck() {
        viewModelScope.launch {
            launch {
                checkTime()
            }
            launch {
                checkFileExist()
            }
        }
    }

    private suspend fun checkTime() {
        delay(1500L)
        timeCheck = true
    }

    private suspend fun checkFileExist() {
        val data = fbApi.getAssetData()
        data.entries.forEach { (key, value) ->
            val fileName = "${key}_${value}.json"
            totalFileName.add(fileName)
            if (!fileHelper.isExistAsset(fileName)) {
                downloadFileSet.add(fileName)
            }
        }
        downloadCheck = true
    }

    var maxCnt by mutableStateOf(0)
    var successCnt by mutableStateOf(0)
    fun startDownload() {
        maxCnt = downloadFileSet.size
        successCnt = 0
        downloadFileSet.forEach { fileName ->
            val savePath = fileHelper.getAssetFilePath(fileName)
            FireStorageReferenceHelper.getAssetReference(fileName)
                .downloadToFile(
                    savePath,
                    successCompletion = {
                        successCnt += 1
                    },
                    failCompletion = {

                    }
                )
        }
    }

    fun readAssetsFile() {
        viewModelScope.launch {
            totalFileName.forEach { fileName ->
                val savePath = fileHelper.getAssetFilePath(fileName)
                if (fileName.startsWith("raid_")) {
                    val jsonString = fileHelper.readFile(savePath)
                    val data: List<RaidTypeItem> = Json.decodeFromString(jsonString)
                    RaidItem.addData(data)
                } else if (fileName.startsWith("synergy_")) {
                    val jsonString = fileHelper.readFile(savePath)
                    val data: Map<String, String> = Json.decodeFromString(jsonString)
                    Synergy.addData(data)
                }
            }
        }
    }
}