package com.wonddak.loacell.di

import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.storage.AssetStorage
import com.wonddak.loacell.storage.CommonFireStorage
import com.wonddak.loacell.storage.getFireStorage
import com.wonddak.loacell.store.CommonFireStore
import com.wonddak.loacell.store.getFireStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
interface IosAppGraph : ViewModelGraph {
    val config: Config
    val fileHelper: FileHelper
    val lostArkApi: LostArkApi
    val assetStorage: AssetStorage

    @Provides
    fun provideFireStorage(): CommonFireStorage = getFireStorage()

    @Provides
    fun provideFireStore(): CommonFireStore = getFireStore()

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): IosAppGraph
    }
}
