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
import com.wonddak.loacell.ui.login.SplashView
import com.wonddak.loacell.ui.raidRoom.RaidRoomView
import com.wonddak.loacell.viewModel.AuthViewModel
import com.wonddak.loacell.viewModel.RaidViewModel
import com.wonddak.loacell.viewModel.SplashViewModel
import com.wonddak.loacell.viewModel.StoreViewModel
import org.koin.compose.koinInject

@Composable
fun LoaCellNavGraph(
    navController: NavHostController,
    splashViewModel: SplashViewModel = koinInject(),
    authViewModel: AuthViewModel = koinInject(),
    storeViewModel: StoreViewModel = koinInject(),
    raidViewModel: RaidViewModel = koinInject(),
) {
    Scaffold(
        topBar = {
            LoaCellTopAppBar(
                navController,
                storeViewModel, raidViewModel
            )
        },
        bottomBar = {
            LoaCellBottomAppBar(
                navController,
                raidViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Const.NAV_SPLASH,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(
                route = Const.NAV_SPLASH
            ) {
                SplashView(
                    splashViewModel,
                    authViewModel,
                    goToMain = {
                        navController.navigate(Const.NAV_MAIN) {
                            popUpTo(Const.NAV_SPLASH) {
                                inclusive = true
                            }
                        }
                    },
                    goToLogin = {
                        navController.navigate(Const.NAV_LOGIN) {
                            popUpTo(Const.NAV_SPLASH) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
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
            ) { _ ->
                RaidRoomView(
                    Modifier.fillMaxSize(),
                    navController,
                    authViewModel, storeViewModel, raidViewModel
                )
            }
            composable(
                route = Const.NAV_RAID_DETAIL
            ) {
                Column {
                    Text("DETAIL")
                }
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

@Composable
fun NavController.isSplash(): Boolean {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route?.let { currentRoute ->
        currentRoute.startsWith(Const.NAV_SPLASH)
    } ?: false
}

@Composable
fun NavController.isRaidRoom(): Boolean {
    val navBackStackEntry by this.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route?.let { currentRoute ->
        currentRoute.startsWith(Const.NAV_ROOM)
    } ?: false
}