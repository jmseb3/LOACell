package com.wonddak.loacell.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wonddak.loacell.Const
import com.wonddak.loacell.model.RaidInfo
import com.wonddak.loacell.ui.login.LoginView
import com.wonddak.loacell.ui.login.SplashView
import com.wonddak.loacell.ui.raidRoom.RaidRoomView
import com.wonddak.loacell.ui.raidRoom.raid.RaidAddView
import com.wonddak.loacell.ui.raidRoom.raid.RaidDetailView
import com.wonddak.loacell.ui.raidRoom.user.UserDetailView
import com.wonddak.loacell.ui.setting.SettingView
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
            .imePadding()
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
            MainView(
                navController,
                authViewModel, storeViewModel, raidViewModel
            )
        }
        composable(route = Const.NAV_SETTING) {
            SettingView(
                authViewModel, storeViewModel,
                navController::depth2toMain
            )
        }

        // 그냥 입장하기 한 경우
        composable(
            route = Const.NAV_ROOM_ENTER_MAIN,
        ) {
            RoomEnterView(
                "",
                storeViewModel.roomList,
                authViewModel.user!!.uid,
                initRoom = {
                    navController.navigate(Const.NAV_RAID_DETAIL_MAIN + it.uniqueId) {
                        launchSingleTop = true
                        popUpTo(Const.NAV_MAIN) {
                            inclusive = false
                        }
                    }
                },
                navController::depth2toMain
            )
        }

        //카카오 공유하기를 눌러 실행한 경우
        composable(
            route = Const.NAV_ROOM_ENTER,
            arguments = listOf(
                navArgument(Const.NAV_ROOM_ENTER_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            ),
            deepLinks = listOf(
                NavDeepLink("kakaoeaad613c8a32160c49991040e94170f9://kakaolink?uniqueId={${Const.NAV_ROOM_ENTER_ARG}}")
            )
        ) { backStackEntry ->
            if (authViewModel.user == null) {
                navController.navigate(Const.NAV_LOGIN) {
                    launchSingleTop = true
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            } else {
                RoomEnterView(
                    backStackEntry.arguments?.getString(Const.NAV_ROOM_ENTER_ARG) ?: "",
                    storeViewModel.roomList,
                    authViewModel.user!!.uid,
                    initRoom = {
                        navController.navigate(Const.NAV_RAID_DETAIL_MAIN + it.uniqueId) {
                            launchSingleTop = true
                            popUpTo(Const.NAV_MAIN) {
                                inclusive = false
                            }
                        }
                    },
                    navController::depth2toMain
                )
            }
        }

        composable(
            route = Const.NAV_ROOM,
        ) { _ ->
            RaidRoomView(
                Modifier.fillMaxSize(),
                authViewModel, storeViewModel, raidViewModel,
                navigateRaidAdd = {
                    navController.navigate(Const.NAV_RAID_ADD) {
                        launchSingleTop = true
                    }
                },
                navigateRaidDetail = { raidId ->
                    navController.navigate(Const.NAV_RAID_DETAIL_MAIN + raidId) {
                        launchSingleTop = true
                    }
                },
                navigateUserDetail = { userName ->
                    navController.navigate(Const.NAV_USER_DETAIL_MAIN + userName) {
                        launchSingleTop = true
                    }
                },
                navController::depth2toMain,
            )
        }
        composable(
            route = Const.NAV_RAID_ADD
        ) {
            val roomInfo = storeViewModel.roomList.find { it.uniqueId == raidViewModel.roomId }
            roomInfo?.let {
                RaidAddView(
                    it.uniqueId,
                    prevData = null,
                    navController::depth3toRoom
                )
            }
        }
        composable<RaidInfo> { backStackEntry ->
            val roomInfo = storeViewModel.roomList.find { it.uniqueId == raidViewModel.roomId }
            val raidInfo: RaidInfo = backStackEntry.toRoute()
            RaidAddView(
                roomInfo!!.uniqueId,
                raidInfo,
                navController::depth3toRoom
            )
        }
        composable(
            route = Const.NAV_RAID_DETAIL,
            arguments = listOf(
                navArgument(Const.NAV_RAID_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val raidId = backStackEntry.arguments?.getString(Const.NAV_RAID_DETAIL_ARG) ?: ""
            val roomInfo = storeViewModel.roomList.find { it.uniqueId == raidViewModel.roomId }
            RaidDetailView(
                roomInfo!!,
                raidId,
                raidViewModel.raidList,
                raidViewModel.userList,
                navigationEdit = {
                    navController.navigate(it) {
                        launchSingleTop = true
                    }
                },
                navController::depth3toRoom
            )
        }
        composable(
            route = Const.NAV_USER_DETAIL,
            arguments = listOf(
                navArgument(Const.NAV_USER_DETAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString(Const.NAV_USER_DETAIL_ARG) ?: ""
            val userInfo = raidViewModel.userList.find { it.name == userName }
            UserDetailView(
                userInfo,
                navController::depth3toRoom
            )
        }
    }
}

fun NavHostController.depth2toMain() {
    this.navigate(Const.NAV_MAIN) {
        this.popUpTo(this@depth2toMain.graph.id) {
            inclusive = true
        }
    }
}

fun NavHostController.depth3toRoom() {
    this.navigate(Const.NAV_ROOM) {
        this.popUpTo(this@depth3toRoom.graph.id) {
            inclusive = true
        }
    }
}