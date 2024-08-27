package com.wonddak.loacell

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor2.KtorNetworkFetcherFactory
import com.wonddak.loacell.theme.AppTheme
import com.wonddak.loacell.ui.main.LoaCellNavGraph
import okio.Path.Companion.toPath
import org.koin.compose.KoinContext

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .memoryCache {
                MemoryCache.Builder()
                    // Set the max size to 25% of the app's available memory.
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(getCacheDir().toPath())
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
    KoinContext {
        val navController: NavHostController = rememberNavController()
        AppTheme {
            LoaCellNavGraph(navController)
        }
    }
}

expect fun getCacheDir(): String

