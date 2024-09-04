package com.wonddak.loacell

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.network.ktor2.KtorNetworkFetcherFactory
import com.wonddak.loacell.theme.AppTheme
import com.wonddak.loacell.ui.main.LoaCellNavGraph
import com.wonddak.loacell.util.FileHelper
import okio.Path.Companion.toPath
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    KoinContext {
        val navController: NavHostController = rememberNavController()
        val fileHelper: FileHelper = koinInject()
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(fileHelper.getCacheImage().replace("file://","").toPath())
                        .maxSizePercent(0.03)
                        .build()
                }
                .build()
        }
        AppTheme {
            LoaCellNavGraph(navController)
        }
    }
}

