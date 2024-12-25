package com.wonddak.loacell

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.wonddak.loacell.theme.AppTheme
import com.wonddak.loacell.ui.main.LoaCellNavGraph
import com.wonddak.loacell.util.FileHelper
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App(
    navController: NavHostController = rememberNavController(),
) {
    KoinContext {
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }
                .diskCache {
                    DiskCache.Builder().directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                        .maxSizeBytes(512L * 1024 * 1024) // 512MB
                        .build()
                }
                .build()
        }
        AppTheme {
            LoaCellNavGraph(navController)
        }
    }
}

