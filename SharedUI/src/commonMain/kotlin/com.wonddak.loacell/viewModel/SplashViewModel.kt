package com.wonddak.loacell.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonddak.loacell.repository.AssetRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey
@Inject
class SplashViewModel(
    private val assetRepository: AssetRepository,
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
        val plan = assetRepository.createFilePlan()
        totalFileName.addAll(plan.allFileNames)
        downloadFileSet.addAll(plan.downloadFileNames)
        downloadCheck = true
    }

    var maxCnt by mutableStateOf(0)
    var successCnt by mutableStateOf(0)

    fun startDownload() {
        maxCnt = downloadFileSet.size
        successCnt = 0
        assetRepository.download(downloadFileSet) {
            successCnt += 1
        }
    }

    fun isAssetDownloaded(fileName: String): Boolean = assetRepository.isDownloaded(fileName)

    fun replaceAsset(fileName: String, onSuccess: () -> Unit, onFailure: () -> Unit) =
        assetRepository.replace(fileName, onSuccess, onFailure)

    fun readAssetsFile() {
        viewModelScope.launch {
            assetRepository.load(totalFileName)
        }
    }
}
