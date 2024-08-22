package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.network.firebase.FBApi
import com.wonddak.loacell.storage.FireStorageReferenceHelper
import com.wonddak.loacell.storage.downloadToFile
import com.wonddak.loacell.util.FileHelper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val fbApi: FBApi,
    private val fileHelper: FileHelper,
) : ViewModel() {

    var timeCheck by mutableStateOf(false)
        private set

    var downloadCheck by mutableStateOf(false)
        private set

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
        Napier.d(tag = "SPLASH2") { data.toString() }
        data.entries.forEach { (key, value) ->
            val fileName = "${key}_${value}.json"
            Napier.d(tag = "SPLASH2") { "fileName : $fileName" }
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
            FireStorageReferenceHelper.getAssetReference(fileName)
                .downloadToFile(
                    fileHelper.getAssetFilePath(fileName),
                    successCompletion = {
                        successCnt += 1
                    },
                    failCompletion = {

                    }
                )
        }
    }
}