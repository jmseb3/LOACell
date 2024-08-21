package com.wonddak.loacell

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wonddak.loacell.theme.AppTheme
import com.wonddak.loacell.ui.main.LoaCellNavGraph
import org.koin.compose.KoinContext

@Composable
fun App() = AppTheme {
    KoinContext {
        val navController: NavHostController = rememberNavController()
        LoaCellNavGraph(navController)
    }
}

