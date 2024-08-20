package com.wonddak.loacell.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wonddak.loacell.Const
import com.wonddak.loacell.ui.main.LoaCellBottomAppBar
import com.wonddak.loacell.ui.main.LoaCellTopAppBar

@Composable
fun LoaCellNavGraph(
    navController: NavHostController,
) {
    Scaffold(
        topBar = {
            LoaCellTopAppBar(navController)
        },
        bottomBar = {
            LoaCellBottomAppBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Const.NAV_MAIN,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Const.NAV_MAIN) {
                Column {

                }
            }
        }
    }
}

@Composable
fun NavController.isMain(): Boolean {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    return currentRoute?.startsWith(Const.NAV_MAIN) ?: false
}