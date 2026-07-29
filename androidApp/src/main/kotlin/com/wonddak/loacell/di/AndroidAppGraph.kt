package com.wonddak.loacell.di

import android.app.Application
import android.content.Context
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.util.FileUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph {
    val config: Config
    val fileHelper: FileHelper
    val lostArkApi: LostArkApi

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    fun provideFileUtil(context: Context): FileUtil = FileUtil(context)

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AndroidAppGraph
    }
}
