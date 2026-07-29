package com.wonddak.loacell.di

import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FileHelper
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
interface IosAppGraph : ViewModelGraph {
    val config: Config
    val fileHelper: FileHelper
    val lostArkApi: LostArkApi

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): IosAppGraph
    }
}
