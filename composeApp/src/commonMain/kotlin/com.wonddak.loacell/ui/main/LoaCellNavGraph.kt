package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.wonddak.loacell.Const
import com.wonddak.loacell.ui.login.LoginView

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
                MainView(Modifier.fillMaxSize(), navController)
            }
            composable(route = Const.NAV_LOGIN) {
                LoginView(Modifier.fillMaxSize(), navController)
            }
            composable(route = "TEST") {
                Column {
                    Text("TEST")
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