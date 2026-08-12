package com.wonddak.loacell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.wonddak.loacell.theme.AppTheme
import com.wonddak.loacell.ui.main.LoaCellNavGraph
import com.wonddak.loacell.di.LocalConfig
import com.wonddak.loacell.di.LocalFileHelper
import com.wonddak.loacell.di.LocalLostArkApi
import com.wonddak.loacell.di.LocalAssetStorage
import com.wonddak.loacell.network.lostark.LostArkApi
import com.wonddak.loacell.util.Config
import com.wonddak.loacell.util.FileHelper
import com.wonddak.loacell.storage.AssetStorage
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import okio.FileSystem

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    metroViewModelFactory: MetroViewModelFactory,
    config: Config,
    fileHelper: FileHelper,
    lostArkApi: LostArkApi,
    assetStorage: AssetStorage,
) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                    .maxSizeBytes(128L * 1024 * 1024)
                    .build()
            }
            .build()
    }
    AppTheme {
        CompositionLocalProvider(
            LocalMetroViewModelFactory provides metroViewModelFactory,
            LocalConfig provides config,
            LocalFileHelper provides fileHelper,
            LocalLostArkApi provides lostArkApi,
            LocalAssetStorage provides assetStorage,
        ) {
            LoaCellNavGraph(
                navController = navController,
            )
        }
    }
}
