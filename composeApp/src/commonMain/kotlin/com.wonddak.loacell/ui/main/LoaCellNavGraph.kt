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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.wonddak.loacell.Const
import com.wonddak.loacell.ui.login.LoginView
import com.wonddak.loacell.ui.raidRoom.RaidRoomView
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.koinInject

@Composable
fun LoaCellNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = koinInject(),
    storeViewModel: StoreViewModel = koinInject(),
    raidViewModel: RaidViewModel = koinInject(),
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
            startDestination = Const.NAV_LOGIN,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(
                route = Const.NAV_LOGIN
            ) {
                LoginView(
                    Modifier.fillMaxSize(),
                    navController,
                    authViewModel, storeViewModel
                )
            }
            composable(route = Const.NAV_MAIN) {
                MainView(
                    Modifier.fillMaxSize(),
                    navController,
                    authViewModel, storeViewModel, raidViewModel
                )
            }
            composable(
                route = Const.NAV_ROOM,
                arguments = listOf(
                    navArgument(Const.ARG_ROOM_ID) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                RaidRoomView(
                    Modifier.fillMaxSize(),
                    backStackEntry.arguments?.getString(Const.ARG_ROOM_ID),
                    authViewModel, storeViewModel, raidViewModel
                )
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
    return navBackStackEntry?.destination?.route?.let { currentRoute ->
        currentRoute.startsWith(Const.NAV_MAIN)
    } ?: false
}

@Composable
fun NavController.isLogin(): Boolean {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route?.let { currentRoute ->
        currentRoute.startsWith(Const.NAV_LOGIN)
    } ?: false
}