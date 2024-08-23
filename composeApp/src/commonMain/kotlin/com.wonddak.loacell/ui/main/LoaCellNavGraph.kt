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
    NavHost(
        navController = navController,
        startDestination = Const.NAV_SPLASH,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(
            route = Const.NAV_SPLASH,
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
            Scaffold(
                topBar = {
                    LoaCellTopAppBar(
                        "LoaCell",
                        null
                    )
                },
                bottomBar = {
                    LoaCellBottomAppBar {

                    }
                }
            ) { innerPadding ->
                MainView(
                    Modifier.fillMaxSize().padding(innerPadding),
                    navController,
                    authViewModel, storeViewModel, raidViewModel
                )
            }
        }
        composable(
            route = Const.NAV_ROOM,
        ) { _ ->
            Scaffold(
                topBar = {
                    LoaCellTopAppBar(
                        raidViewModel.roomInfo?.title ?: "",
                    ) {
                        navController.navigate(Const.NAV_MAIN) {
                            popUpTo(Const.NAV_ROOM) {
                                inclusive = true
                            }
                        }
                    }
                },
                bottomBar = {
                    LoaCellBottomAppBar {
                        RaidRoomActions(raidViewModel)
                    }
                }
            ) { innerPadding ->
                RaidRoomView(
                    Modifier.fillMaxSize().padding(innerPadding),
                    navController,
                    authViewModel, storeViewModel, raidViewModel
                )
            }
        }
        composable(
            route = Const.NAV_RAID_DETAIL,
            arguments = listOf(navArgument(Const.NAV_RAID_DETAIL_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val raidId = backStackEntry.arguments?.getString(Const.NAV_RAID_DETAIL_ARG) ?: "error"
            Scaffold(
                topBar = {
                    LoaCellTopAppBar(
                        raidViewModel.roomInfo?.title ?: "",
                    ) {
                        navController.navigate(Const.NAV_ROOM) {
                            popUpTo(Const.NAV_RAID_DETAIL) {
                                inclusive = true
                            }
                        }
                    }
                },
                bottomBar = {
                    LoaCellBottomAppBar {

                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    Text(Const.NAV_RAID_DETAIL)
                    Text(raidId)
                }
            }
        }
        composable(
            route = Const.NAV_USER_DETAIL,
            arguments = listOf(navArgument(Const.NAV_USER_DETAIL_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString(Const.NAV_USER_DETAIL_ARG) ?: "error"
            Scaffold(
                topBar = {
                    LoaCellTopAppBar(
                        "User",
                    ) {
                        navController.navigate(Const.NAV_ROOM) {
                            popUpTo(Const.NAV_USER_DETAIL) {
                                inclusive = true
                            }
                        }
                    }
                },
                bottomBar = {
                    LoaCellBottomAppBar {

                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    Text(Const.NAV_USER_DETAIL)
                    Text(userName)
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